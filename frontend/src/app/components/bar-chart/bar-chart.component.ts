import { Component, OnInit, Input, ElementRef, ViewChild, ViewEncapsulation, ViewContainerRef } from '@angular/core';
import * as d3 from 'd3';
import { DashboardComponent } from 'src/app/pages/dashboard/dashboard.component';
declare var $: any;

@Component({
  selector: 'sdrc-bar-chart',
  templateUrl: './bar-chart.component.html',
  styleUrls: ['./bar-chart.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class BarChartComponent implements OnInit {

  @ViewChild('barChart') private chartContainer: ElementRef;
  @Input('data') private data: Array<any>;

  constructor(private hostRef: ElementRef, private viewContainerRef: ViewContainerRef) { }

  ngOnInit() {
    if (this.data) {
      this.createChart(this.data);
    }
  }

  ngOnChanges(changes) {
    if (this.data && changes.data.previousValue) {
      this.createChart(this.data);
    }
  }

  createChart(data) {
    let el = this.chartContainer.nativeElement;
    d3.select(el).select("svg").remove();
    var n = data.length, // number of layers
      m = 10 // number of samples per layer
    var layers = data;
    layers.forEach(function (layer, i) {
      layer.forEach(function (el, j) {
        el.y = undefined;
        el.y0 = i;
      });
    });
    var allNullValues = true;
    var margin = {
      top: 20,
      right: 20,
      bottom: 60,   // bottom height
      left: 40
    }, width = $(this.hostRef.nativeElement).parent().width() - margin.left - margin.right, height = $(this.hostRef.nativeElement).parent().height() - margin.top - margin.bottom // //
      // height
      - margin.top - margin.bottom + 10;

    let x = d3.scaleBand()
    .domain(data[0]
      .map(function (d) {
      return d.axis;
    })).range([0, width])
    .padding(0.1);    

    var mergedArray = Array.prototype.concat.apply([], data);
    let max = d3.max(mergedArray.map(function (d) {
      return d.value;
    }));

    max = 100
    //console.log(max)
    let y = d3.scaleLinear()
    .domain([0, max])
    .rangeRound([height, 0]);

    var color = ["#f2beaa", "#d85c54", "#795343"];

    var formatTick = function (d) {
      return d.split(".")[0];
    };

    var xAxis = d3.axisBottom().scale(x).tickFormat(formatTick);
    const xBandwidth = x.bandwidth() > 40 * data.length ? 40 * data.length : x.bandwidth();
    var svg = d3.select(el)
    .append("svg")
    .attr("id", "columnbarChart")
    .attr("width", width + margin.left + margin.right)
    .attr("height", height + margin.top + 70 + margin.bottom)
    .append("g")
    .attr( "transform", "translate(" + margin.left + "," + margin.top + ")");
   

    var layer = svg.selectAll(".layer")
      .data(layers)
      .enter()
      .append("g")
      .attr("class", "layer")
      .style("fill",
        function (d, i) {
          return color[i];
        }).attr("id", function (d, i) {
          return i;
        });
    for (let j = 0; j < data.length; j++) {
      for (let index = 0; index < data[j].length; index++) {
        if (data[j][index].value != null) {
          allNullValues = false;
          break;
        }
      }
    }

    if (allNullValues == false) {
      var rect = layer.selectAll("rect")
      .data(function (d) {
        return d;
      })
      .enter()
      .append("rect")
      .attr("x", function (d) {
        return x(d.axis) + (x.bandwidth() - xBandwidth) / 2;
      })
      .attr("y", height)
      .attr("width", xBandwidth)
      .attr("height", 0)
      .attr("stroke", function (d) {
        if (d.value) {
          return '#fff'
        }
        else {
          return '#DDD'
        }
      })
      .attr("stroke-width", "2px")
      .attr("class", function (d, i) {
          return d.cssClass;
        })
      .on("click",  (d)=>{
        this.getParentComponent().openIndividualLineChart(d.serverData)
      })  
      .on("mouseover", function (d) {
          showPopover.call(this, d);
        })
      .on("mouseout", function (d) {
          removePopovers();
        });
    } else {
      if (width <= 360) {
        svg.append("g").append("text")
          .attr("transform", "translate(-30,0)")
          .attr("x", 40)
          .attr("y", 50)
          .attr("font-size", "16px")
          .text("Data Not Available");
      } else {
        svg.append("g").append("text")
          .attr("transform", "translate(120,0)")
          .attr("x", 40)
          .attr("y", 50)
          .attr("font-size", "18px")
          .text("Data Not Available");
      }
    }

    

    svg.append("g")
    .attr("class", "x axis")
    .attr("transform", "translate(0," + height + ")")
    .call(xAxis)
    .selectAll("text")
    .call(wrap, x.bandwidth());

    var yAxis = d3.axisLeft().scale(y).ticks(10);

    svg.append("g")
    .attr("class", "y axis")
    .call(yAxis)
    .append("text")
    .attr("transform", "rotate(-90)")
    .attr("y", 0 - margin.left)
    .attr("x", 0 - (height / 2))
    .attr("dy", "1em")
    .attr("text-anchor", "end")
    

    svg.append("text")
      .attr("class", "y axis")
      .attr("text-anchor", "end")
      .attr("y", 0)
      .attr("dy", "-30px")
      .attr("dx", "-30px")
      .attr("transform", "rotate(-90)")
      .text("Percentage")
      .style("font-size", "9px");


    function transitionGrouped() {
      y.domain([0, max]);

      rect.transition()
      .duration(500)
      .delay(function (d, i) {
        return i * 10;
      })
      .attr("x",(d, i)=> {
        let barChartData: IBarChartData = d
        if(d.y0 === 0){
          if(barChartData.hasMRT){
            return ((xBandwidth / n) + x(d.axis) + (x.bandwidth() - xBandwidth) / 2 + xBandwidth / 10 * d.y0)-(xBandwidth/7);
          }else{
            return ((xBandwidth / n) + x(d.axis) + (x.bandwidth() - xBandwidth) / 2 + xBandwidth / 10 * d.y0)-(xBandwidth/15);
          }
          
        }else if(d.y0 === 1){
          if(d.value){
            return ((xBandwidth / n) + x(d.axis) + (x.bandwidth() - xBandwidth) / 2 + xBandwidth / 10 * d.y0)-(xBandwidth/30);
          }
        }
        else{
          if(barChartData.hasMRT){
            return ((xBandwidth / n) + x(d.axis) + (x.bandwidth() - xBandwidth) / 2 + xBandwidth / 10 * d.y0) + (xBandwidth/13);
          }else{
            return ((xBandwidth / n) + x(d.axis) + (x.bandwidth() - xBandwidth) / 2 + xBandwidth / 10 * d.y0) + (xBandwidth/320);
          }
        }
        
      })
      .attr("width", xBandwidth / 5)
      .transition()
      .attr("y", function (d) {
          return y(d.value);
        }).attr("height", function (d) {
          return height - y(d.value);
        });
    }

    transitionGrouped();
    function removePopovers() {
      $('.popover').each(function () {
        $(this).remove();
      });
    }
    function showPopover(d) {
      $(this).popover(
        {
          title: '',
          placement: 'top',
          container: 'body',
          trigger: 'manual',
          html: true,
          animation: false,
          content: function () {
            return "<div style='color: #257ab6;'>" + d.axis + "</div>" + (d.legend!='Baseline'?(d.legend + "( " + d.tp + ")" ):'Baseline')+  
            " Value : " + d.value + "<br>" + "Source : " + d.src;
          }
        });
      $(this).popover('show');
    }

    //============Text wrap function in x-axis of column chart=====================
    function wrap(text, width) {
      text.each(function() {
        var text = d3.select(this),
            words = text.text().split(/\s+/).reverse(),
            word,
            line = [],
            lineNumber = 0,
            lineHeight = 1.1, // ems
            y = text.attr("y"),
            dy = parseFloat(text.attr("dy")),
            tspan = text.text(null).append("tspan").attr("x", 0).attr("y", y).attr("font-size", "10px").attr("dy", dy + "em");
        while (word = words.pop()) {
          line.push(word);
          tspan.text(line.join(" "));
          if (tspan.node().getComputedTextLength() > width) {
            line.pop();
            tspan.text(line.join(" "));
            line = [word];
            tspan = text.append("tspan").attr("x", 0).attr("y", y).attr("dy", ++lineNumber * lineHeight + dy + "em").attr("font-size", "10px").text(word);
          }
        }
      });
    }

    //NEW CODE FOR DATA VALUE TEXT ON EACH BAR-----------------
    var e0Arr = [];
    for (var i = 0; i < data.length; i++) {
      e0Arr.push(data[i][0].value);
      layer.selectAll("evmbartext" + i).data(data[i]).enter()
        .append("text")
        .attr("x", function (d) {
          let barChartData: IBarChartData = d
          if(d.legend == "Baseline"){            
            if(barChartData.hasMRT){
              return (xBandwidth/ 8) + x(d.axis) + (x.bandwidth() - xBandwidth) / 2 + xBandwidth / (2 * data.length) + (xBandwidth / data.length * i);
            }else{
              return (xBandwidth/ 5) + x(d.axis) + (x.bandwidth() - xBandwidth) / 2 + xBandwidth / (2 * data.length) + (xBandwidth / data.length * i);
            }
            
          }else if(d.legend == "Target"){
            if(barChartData.hasMRT){
              return x(d.axis) - (xBandwidth/ 8)  + (x.bandwidth() - xBandwidth) / 2 + xBandwidth / (2 * data.length) + (xBandwidth / data.length * i);
            }else{
              return x(d.axis) - (xBandwidth/ 5)  + (x.bandwidth() - xBandwidth) / 2 + xBandwidth / (2 * data.length) + (xBandwidth / data.length * i);
            }
            
          }else{
            if(d.value){
              return x(d.axis) + (x.bandwidth() - xBandwidth) / 2 + xBandwidth / (2 * data.length) + (xBandwidth / data.length * i);
            }else{
              return ""
            }
          }
            
        })
        .attr("y", function (d) {
          return y(d.value) - 3;
        }).style("font-size", "9px")
        .style("text-anchor", "middle")
        .style("fill", "#000000")
        .text(function (d) {
          if(d.legend === "MRT" && !d.hasMRT){
            return null
          }else{
           
            return Math.round(d.value);
          }
            
        });
    }
  }

  getParentComponent(): DashboardComponent{
    return this.viewContainerRef[ '_data' ].componentView.component.viewContainerRef[ '_view' ].component
  }
}
