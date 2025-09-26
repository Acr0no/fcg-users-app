import {Component, Inject, OnInit} from '@angular/core';
import {UserService} from '../../../services/user.service';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {User} from '../../../interfaces/user';
import {AppConstants} from '../../../constants/app-constants';

@Component({
  selector: 'app-user-dialog',
  templateUrl: './user-dialog.component.html',
  styleUrl: './user-dialog.component.scss'
})
export class UserDialogComponent implements OnInit {
  fields: FormGroup;
  isEdit = false;
  hasErrorMsg: boolean = false;
  errorMsg : string = '';

  constructor(private userService: UserService,
              private fb: FormBuilder,
              private dialogRef: MatDialogRef<UserDialogComponent>,
              @Inject(MAT_DIALOG_DATA) public data: any | null) {

    this.fields = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      name: ['', [Validators.required]],
      surname: ['', [Validators.required]],
      address: [''],
    });
  }

  ngOnInit(): void {
    if (this.data.isEdit) {
      let userId: number = this.data.user.id;
      this.userService.getUserById(AppConstants.addOrUpdateUserEndpoint, userId).subscribe({
        next: res => {
          this.fields = this.fb.group({
            email: [res.email, [Validators.required, Validators.email]],
            name: [res.name, [Validators.required]],
            surname: [res.surname, [Validators.required]],
            address: [res.address ?? "N/D"],
          });
        }
      })
    }
  }


  submit(): void {
    if (this.fields.invalid) {
      this.fields.markAllAsTouched();
      return;
    }
    const user: User = this.fields.value;
    if (this.data.isEdit) {
      let userId: number = this.data.user.id;
      this.userService.updateUser(AppConstants.addOrUpdateUserEndpoint, userId, user).subscribe({
        next: res => {
          this.dialogRef.close(res)
        },
        error: (err) => {
          this.hasErrorMsg = true;
          this.errorMsg = err.error?.error_description
        }
      })
    } else {
      console.log("add");
      this.userService.addUser(AppConstants.addOrUpdateUserEndpoint, user).subscribe({
        next: res => {
          this.dialogRef.close(res)
        },
        error: (err) => {
          this.hasErrorMsg = true;
          this.errorMsg = err.error?.error_description
        }
      })
    }
  }

  cancel(): void {
    this.dialogRef.close(null);
  }

}
