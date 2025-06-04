import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import {FormGroup, ReactiveFormsModule} from "@angular/forms";

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { GeneralLayoutComponent } from './component/general-layout/general-layout.component';
import { FooterComponent } from './component/general-layout/footer/footer.component';
import { NavbarComponent } from './component/general-layout/navbar/navbar.component';
import { SidebarComponent } from './component/general-layout/sidebar/sidebar.component';
import { HomeComponent } from './component/pages/home/home.component';
import { LoginComponent } from './component/pages/login/login.component';
import { RegisterComponent } from './component/pages/register/register.component';
import { TokenInterceptor } from './token.interceptor';
import { ButtonModule } from "primeng/button";
import { TableModule } from "primeng/table";
import { NotifierModule, NotifierService } from "angular-notifier";
import { LogoutComponent } from './component/pages/logout/logout.component';
import { CoursesComponent } from './courses/courses.component';
import { CreateCoursesComponent } from './create-courses/create-courses.component';
import { FormsModule } from '@angular/forms';

@NgModule({
  declarations: [
    AppComponent,
    GeneralLayoutComponent,
    FooterComponent,
    NavbarComponent,
    SidebarComponent,
    HomeComponent,
    LoginComponent,
    RegisterComponent,
    LogoutComponent,
    CoursesComponent,
    CreateCoursesComponent
  ],
  imports: [
    FormsModule,
    BrowserModule,
    AppRoutingModule,
    ButtonModule,
    TableModule,
    HttpClientModule,
    NotifierModule,
    ReactiveFormsModule
  ],
  providers: [
    NotifierService,
    { provide: HTTP_INTERCEPTORS, useClass: TokenInterceptor, multi: true }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
