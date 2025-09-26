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

  /** Boolean to error banner visibility when an API error occurs. */
  hasErrorMsg: boolean = false;

  /** Error message coming from BE */
  errorMsg: string = '';

  /** true when the dialog is in "Add" mode. */
  isAdd: boolean = false;

  /** true when the dialog is in "Edit" mode. */
  isEdit: boolean = false;

  /** true when the dialog is in "Delete" mode. */
  isDelete: boolean = false;

  //used to show user name and surname in delete mode
  userName: string = '';
  userSurname: string = '';


  constructor(
    private userService: UserService,
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<UserDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any | null
  ) {
    this.fields = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      name: ['', [Validators.required]],
      surname: ['', [Validators.required]],
      address: [''],
    });

    // Dialog check mode (edit or delete)
    if (this.data.user) {
      this.isEdit = !this.data.isAdd && this.data.isEdit && !this.data.isDelete;
      this.isDelete = !this.data.isAdd && !this.data.isEdit && this.data.isDelete;

      this.userName = this.data.user.name;
      this.userSurname = this.data.user.surname;
    }
  }

  /**
   * On editing, retrieve the user data and populates the form with  values.
   */
  ngOnInit(): void {
    if (this.isEdit) {
      let userId: number = this.data.user.id;
      this.userService.getUserById(AppConstants.addOrUpdateOrDeleteUserEndpoint, userId).subscribe({
        next: res => {
          this.fields = this.fb.group({
            email: [res.email, [Validators.required, Validators.email]],
            name: [res.name, [Validators.required]],
            surname: [res.surname, [Validators.required]],
            address: [res.address ?? 'N/D'],
          });
        }
      });
    }
  }

  /**
   * Submits the dialog on "Add" or "Edit" mode.
   * On success, closes the dialog with the backend response.
   * On error, shows the error banner with the backend message.
   */
  submit(): void {
    if (this.fields.invalid) {
      this.fields.markAllAsTouched();
      return;
    }
    const user: User = this.fields.value;

    //edit mode
    if (this.isEdit) {
      let userId: number = this.data.user.id;
      this.userService.updateUser(AppConstants.addOrUpdateOrDeleteUserEndpoint, userId, user).subscribe({
        next: res => {
          this.dialogRef.close(res);
        },
        error: (err) => {
          this.hasErrorMsg = true;
          this.errorMsg = err.error?.error_description;
        }
      });
    } //add mode
    else {
      this.userService.addUser(AppConstants.addOrUpdateOrDeleteUserEndpoint, user).subscribe({
        next: res => {
          this.dialogRef.close(res);
        },
        error: (err) => {
          this.hasErrorMsg = true;
          this.errorMsg = err.error?.error_description;
        }
      });
    }
  }

  /**
   * Close the dialog
   */
  cancel(): void {
    this.dialogRef.close(null);
  }

  /**
   * Deletes a selected user
   *
   * @param userId ID of the user to delete.
   */
  deleteUser(userId: number): void {
    this.userService.deleteUserById(AppConstants.addOrUpdateOrDeleteUserEndpoint, userId).subscribe({
      next: res => {
        this.dialogRef.close(res);
      },
      error: (err) => {
        this.hasErrorMsg = true;
        this.errorMsg = err.error?.error_description;
      }
    });
  }
}
