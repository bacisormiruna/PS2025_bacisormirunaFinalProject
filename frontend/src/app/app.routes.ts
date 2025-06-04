import { Routes } from "@angular/router";
import { GeneralLayoutComponent } from "./component/general-layout/general-layout.component";
import { HomeComponent } from "./component/pages/home/home.component";
import { LoginComponent } from "./component/pages/login/login.component";
import { LogoutComponent } from "./component/pages/logout/logout.component";
import { AuthGuard } from "./auth.guard";

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'logout', component: LogoutComponent },
  {
    path: '',
    component: GeneralLayoutComponent,
    canActivate: [AuthGuard],
    children: [
      { path: '', redirectTo: 'home', pathMatch: 'full' },
      { path: 'home', component: HomeComponent }
    ]
  },

  { path: '**', redirectTo: 'login', pathMatch: 'full' }
];
