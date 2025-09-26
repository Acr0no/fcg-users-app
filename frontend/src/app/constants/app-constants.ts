/**
 *
 * Used across services and components to avoid hardcoding strings.
 */
export class AppConstants {
  /**
   * Base URL for the backend API.
   */
  public static readonly apiUrl: string = 'http://localhost:8080/api/v1/';

  /**
   * Background color used for ngx spinner.
   */
  public static readonly spinnerBgColor: string = 'rgba(0, 0, 0, 0.25)';

  /**
   * Color used for ngx spinner.
   */
  public static readonly spinnerColor: string = '#4758B8';

  /**
   * Endpoint for retrieving all users.
   */
  public static readonly getUsersEndpoint: string = 'users';

  /**
   * Endpoint path for adding, updating, or deleting a user.
   */
  public static readonly addOrUpdateOrDeleteUserEndpoint: string = 'user';

  /**
   * Endpoint path for uploading a CSV file containing users.
   */
  public static readonly loadUserCsvFileEndpoint: string = 'upload-user-csv';
}
