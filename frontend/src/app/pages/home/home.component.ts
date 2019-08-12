import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { DatePipe } from '@angular/common';
import { AppService } from 'src/app/app.service';
import { StorageService } from 'src/app/services/storage/storage.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
  providers: [DatePipe]
})


export class HomeComponent implements OnInit {
  

  
  constructor(private router: Router, private storageService: StorageService, public appService: AppService) { }

  ngOnInit() {
  }
  logout(){
    this.storageService.remove("access_token")
    this.storageService.remove("refresh_token")
    this.storageService.remove("user_details")
    this.router.navigateByUrl('/');
  }

  
}