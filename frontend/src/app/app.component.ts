import { Component } from '@angular/core';
declare var $:any;

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'cap';

  ngAfterViewInit(){
    $(".main-content").css("min-height", $(window).height()-130);
  }

  ngAfterViewChecked() {
    if ($(window).width() <= 992) {
      $(".collapse").removeClass("show");
      $(".navbar-nav .nav-item").not('.dropdown').click(function () {
        $(".collapse").removeClass("show");
      })
    }
  }
}
