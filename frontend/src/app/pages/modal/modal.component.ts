import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-modal',
  templateUrl: './modal.component.html',
  styleUrls: ['./modal.component.css']
})
export class ModalComponent implements OnInit {

str:any;


  constructor() { }

  ngOnInit() {
    this.str="Lorem Ipsum is simply dummy ; text of the printing; and typesetting industry";
  }

//   saveTextAsFile (data, filename){

  

//     var blob = new Blob([data], {type: 'text/plain'});
//     var e = document.createEvent('MouseEvents'),
//     a = document.createElement('a');

//   a.download = filename;
//   a.href = window.URL.createObjectURL(blob);
//   a.dataset.downloadurl = ['text/plain', a.download, a.href].join(':');
//   e.initEvent('click', true, false);
//   a.dispatchEvent(e);

// }

saveTextAsFile(filename, text) {
  var element = document.createElement('a');
  element.setAttribute('href', 'data:text/plain;charset=utf-8,' + encodeURIComponent(text));
  element.setAttribute('download', filename);

  element.style.display = 'none';
  document.body.appendChild(element);

  element.click();

  document.body.removeChild(element);
}
   expFile(value) {
     let valArr: any=[];
     for(let i=0; i<value.length ; i++){    
      // console.log(value[i]) ;
      //valArr.push(value[i].replace(",","")+'\r\n');
     }
     
    var fileName = "errorlog.txt"
    this.saveTextAsFile(fileName, value );
    }

}
