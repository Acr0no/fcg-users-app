import {AfterViewInit, Component, ElementRef, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {UserService} from '../../services/user.service';
import {MatTableDataSource} from '@angular/material/table';
import {FormBuilder, FormGroup} from '@angular/forms';
import {User} from '../../interfaces/user';
import {NgxSpinnerService} from 'ngx-spinner';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {debounceTime, distinctUntilChanged} from 'rxjs/operators';
import {Page} from '../../interfaces/page';
import {merge, Subject, Subscription} from 'rxjs';
import {MatDialog, MatDialogRef} from '@angular/material/dialog';
import {UserDialogComponent} from '../dialog/user-dialog/user-dialog.component';
import {AppConstants} from '../../constants/app-constants';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit, AfterViewInit, OnDestroy {
  /**
   * Displayed columns form the Material table.
   */
  displayedColumns = ['id', 'name', 'surname', 'email', 'address', 'actions'];

  /**
   * Data source
   */
  dataSource = new MatTableDataSource<User>([]);

  /**
   * Reactive form group for filters
   */
  filters: FormGroup;

  /**
   * Total number of items for pagination.
   */
  totalItems: number = 0;

  /**
   * True when the current query returns zero results.
   */
  isTableEmpty: boolean = false;

  /**
   * Tracks the last CSV file name uploaded to avoid duplicate uploads.
   */
  lastUploadedFileName: string | null = null;
  subscriptions: Subscription[] = [];

  //image logo path
  logo: string = 'assets/logo.svg';

  /**
   * Used to show a mat badge with content of the user added.
   */
  isUserAdded: boolean = false;

  /**
   * Used to show a mat badge with content of the user edited.
   */
  isUserEdited: boolean = false;

  /**
   * Used to show a mat badge with content of the user deleted.
   */
  isUserDeleted: boolean = false;


  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  /**
   * Hidden file input for CSV file
   */
  @ViewChild('fileInput') fileInput!: ElementRef<HTMLInputElement>;

  /**
   * Subject used to explicitly signal that filters have changed
   */
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

  /**
   *  Filter changes trigger with debouncing and distinct checks. Resets the paginator page
   * to 0 on each filter change.
   */
  ngOnInit(): void {
    this.subscriptions.push(
      this.filters.valueChanges
        .pipe(
          debounceTime(200),
          distinctUntilChanged()
        )
        .subscribe(() => {
          if (this.paginator) this.paginator.pageIndex = 0;
          this.filtersChanged$.next();
        })
    );
  }

  /**
   * Pagination and sorting streams with filter changes
   * to trigger a reload; performs the initial load.
   */
  ngAfterViewInit(): void {
    this.subscriptions.push(
      merge(this.sort.sortChange, this.paginator.page, this.filtersChanged$)
        .subscribe(() => this.loadPage())
    );
    this.loadPage();
  }

  /**
   * Loads a page of users from the BE using the current
   * filter, sort and pagination state. Shows a spinner while loading.
   *
   */
  private loadPage(): void {
    const name = (this.filters.value.name ?? '').trim() || undefined;
    const surname = (this.filters.value.surname ?? '').trim() || undefined;
    const sort = this.sort?.active ? `${this.sort.active},${this.sort.direction || 'asc'}` : undefined;

    //spinner
    setTimeout(() => this.spinner.show('dashboard'), 0);

    this.subscriptions.push(
      this.userService.getUsers(AppConstants.getUsersEndpoint, {
        page: this.paginator?.pageIndex ?? 0,
        size: this.paginator?.pageSize ?? 50,
        sort,
        name,
        surname
      }).subscribe({
        next: (res: Page<User>) => {
          const pageSize = this.paginator?.pageSize ?? res.size ?? 50;
          const lastPage = Math.max(0, Math.ceil(res.totalElements / pageSize) - 1);

          // If the current page is out of bounds (after deletions), adjust and reload page
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
        complete: () =>
          //hide spinner
          this.spinner.hide('dashboard')
      })
    );
  }

  /**
   * Opens the user dialog in "Add" mode.
   * On success, reloads the table and shows a mat badge with the content of the user added.
   */
  addUser(): void {
    const dialogRef: MatDialogRef<any> = this.dialog.open(UserDialogComponent, {
      data: {isAdd: true, isEdit: false, isDelete: false}
    });

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

  /**
   * Opens the user dialog in "Edit" mode.
   * On success, reloads the table and shows a mat badge with the content of the user edited.
   * @param user The user to edit.
   */
  editUser(user: User): void {
    const dialogRef: MatDialogRef<any> = this.dialog.open(UserDialogComponent, {
      data: {user, isAdd: false, isEdit: true, isDelete: false}
    });

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

  /**
   * Opens the user dialog in "Delete" mode.
   * On success, reloads the table and shows a mat badge with the content of the user deleted.
   * @param user The user to delete.
   */
  deleteUser(user: User): void {
    const dialogRef: MatDialogRef<any> = this.dialog.open(UserDialogComponent, {
      data: {user, isAdd: false, isEdit: false, isDelete: true}
    });

    dialogRef.afterClosed().subscribe({
      next: res => {
        if (res) {
          this.isUserDeleted = true;
          this.loadPage();
          setTimeout(() => this.isUserDeleted = false, 2000);
        }
      }
    });
  }

  /**
   * Triggers the hidden file input to select a CSV file.
   *
   * @param input The hidden file input element reference.
   */
  openFilePicker(input: HTMLInputElement): void {
    input.value = '';
    input.click();
  }

  /**
   * Handles CSV file selection with validation for file extension and name.
   * @param event Change event emitted by the hidden file input.
   */
  handleFileChange(event: Event): void {
    this.spinner.show('dashboard');
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) {
      this.spinner.hide('dashboard');
      return;
    }

    if (!file.name.toLowerCase().endsWith('.csv')) {
      alert('Selezionare un file .csv');
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

    this.subscriptions.push(
      this.userService.uploadUsersCsv(AppConstants.loadUserCsvFileEndpoint, file).subscribe({
        next: () => {
          this.spinner.hide('dashboard');
          this.loadPage();
        },
        error: (err) => {
          const error = err?.error?.error_description;
          alert(error);
        }
      })
    );
  }

  /**
   * unsubscribe all subscriptions
   */
  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }
}
