import {Component, OnInit} from '@angular/core';
import {MatTableDataSource} from '@angular/material/table';
import {UserServiceService} from './services/user-service.service';
import {UserInterface} from './interfaces/user-interface';
import {FormBuilder, FormControl, FormGroup} from '@angular/forms';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent implements OnInit {

  title = 'angular-root';
/*  filters: FormGroup;
  dataSource: MatTableDataSource<UserInterface> = new MatTableDataSource<UserInterface>();

  constructor(private userService: UserServiceService, private fb: FormBuilder) {
    this.filters = this.fb.nonNullable.group<UserInterface>({
      id: 0,
      email: "",
      name: "",
      surname: "",
      address: "",
    });
  }*/


  ngOnInit(): void {

  }
}
