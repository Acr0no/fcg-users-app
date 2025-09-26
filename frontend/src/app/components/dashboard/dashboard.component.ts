import {AfterViewInit, Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {UserService} from '../../services/user.service';
import {MatTableDataSource} from '@angular/material/table';
import {FormBuilder, FormGroup} from '@angular/forms';
import {User} from '../../interfaces/user';
import {NgxSpinnerService} from 'ngx-spinner';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {debounceTime, distinctUntilChanged} from 'rxjs/operators';
import {Page} from '../../interfaces/page';
import {merge, Subject} from 'rxjs';
import {MatDialog, MatDialogRef} from '@angular/material/dialog';
import {UserDialogComponent} from '../dialog/user-dialog/user-dialog.component';
import {AppConstants} from '../../constants/app-constants';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit, AfterViewInit {
  displayedColumns = ['id', 'name', 'surname', 'email', 'address', 'actions'];
  dataSource = new MatTableDataSource<User>([]);
  filters: FormGroup;
  totalItems: number = 0;
  isTableEmpty: boolean = false;
  lastUploadedFileName: string | null = null;


  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  @ViewChild('fileInput') fileInput!: ElementRef<HTMLInputElement>;

  private filtersChanged$ = new Subject<void>();

  constructor(
    private userService: UserService,
    private fb: FormBuilder,
    private spinner: NgxSpinnerService,
    private dialog: MatDialog
  ) {
    this.filters = this.fb.group({
      name: [''],
      surname: ['']
    });
  }

  ngOnInit(): void {
    this.filters.valueChanges
      .pipe(debounceTime(200), distinctUntilChanged())
      .subscribe(() => {
        if (this.paginator) this.paginator.pageIndex = 0;
        this.filtersChanged$.next();
      });
  }

  ngAfterViewInit(): void {
    merge(this.sort.sortChange, this.paginator.page, this.filtersChanged$)
      .subscribe(() => this.loadPage());
    this.loadPage();
  }


  private loadPage(): void {
    const name = (this.filters.value.name ?? '').trim() || undefined;
    const surname = (this.filters.value.surname ?? '').trim() || undefined;

    setTimeout(() => this.spinner.show('dashboard'), 0);

    this.userService.getUsers(AppConstants.getUsersEndpoint, {
      page: this.paginator?.pageIndex ?? 0,
      size: this.paginator?.pageSize ?? 50,
      name,
      surname
    }).subscribe({
      next: (res: Page<User>) => {
        const pageSize = this.paginator?.pageSize ?? res.size ?? 50;
        const lastPage = Math.max(0, Math.ceil(res.totalElements / pageSize) - 1);

        if (this.paginator && this.paginator.pageIndex > lastPage) {
          this.paginator.pageIndex = lastPage;
          this.loadPage();
          return;
        }

        this.isTableEmpty = res.totalElements === 0;
        this.dataSource.data = res.content;
        this.totalItems = res.totalElements;
      },
      error: (err) => console.error(err),
      complete: () => this.spinner.hide('dashboard')
    });
  }

  isUserAdded: boolean = false;
  isUserEdited: boolean = false;

  addUser(): void {
    const dialogRef: MatDialogRef<any> = this.dialog.open(UserDialogComponent,
      {
        data: {
          isEdit: false
        }
      }
    );
    dialogRef.afterClosed().subscribe({
      next: res => {
        if (res) {
          this.isUserAdded = true;
          this.loadPage();
          setTimeout(() => this.isUserAdded = false, 2000);
        }
      }
    });
  }

  editUser(user: User): void {
    const dialogRef: MatDialogRef<any> = this.dialog.open(UserDialogComponent,
      {
        data: {
          user: user,
          isEdit: true
        }
      }
    );

    dialogRef.afterClosed().subscribe({
      next: res => {
        if (res) {
          this.isUserEdited = true;
          this.loadPage();
          setTimeout(() => this.isUserEdited = false, 2000);
        }
      }
    });
  }

  //Trigger the file prompt
  openFilePicker(input: HTMLInputElement): void {
    input.value = '';
    input.click();
  }

  handleFileChange(event: Event): void {
    this.spinner.show('dashboard');
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) {
      this.spinner.hide('dashboard');
      return;
    }


    if (!file.name.toLowerCase().endsWith('.csv')) {
      alert("Selezionare un file .csv")
      input.value = '';
      this.spinner.hide('dashboard');
      return;
    }

    if (this.lastUploadedFileName === file.name) {
      alert('Hai già caricato questo file');
      input.value = '';
      this.spinner.hide('dashboard');
      return;
    }

    this.lastUploadedFileName = file.name;

    this.userService.uploadUsersCsv(AppConstants.loadUserCsvFileEndpoint, file).subscribe({
      next: res => {
        this.spinner.hide('dashboard');
        this.loadPage();
      },
      error: (err) => {
        const error = err?.error?.error_description;
        alert(error);
      }
    });
  }
}
