import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environment} from "../../environments/environment";
import {Observable} from "rxjs";
import {User} from "../model/user.model";
import {UserViewModel} from "../model/userView.model";

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private userBaseUrl = environment.baseUrl + 'user/'

  constructor(private httpClient: HttpClient) { }

  displayAllUserView(): Observable<UserViewModel[]> {
    return this.httpClient.get<UserViewModel[]>(this.userBaseUrl + 'getAll',
      { headers:
        {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer ' + sessionStorage.getItem("token")
        }
      })
  }

  displayUserViewByEmail(email: string): Observable<UserViewModel> {
    return this.httpClient.get<UserViewModel>(this.userBaseUrl + 'getUserByEmail/' + email,
      { headers:
          {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + sessionStorage.getItem("token")
          }
      })
  }

  processAddUserForm(user : User): Observable<any> {
    return this.httpClient.post<any>(this.userBaseUrl + 'create', user,
      { headers:
          {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + sessionStorage.getItem("token")
          }
      })
  }

}
