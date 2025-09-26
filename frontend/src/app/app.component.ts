import {Component} from '@angular/core';
import {AppConstants} from './constants/app-constants';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {

  spinnerBgColor : string = AppConstants.spinnerBgColor;
  spinnerColor : string = AppConstants.spinnerColor;
}
