import { Component, OnInit, ViewEncapsulation, OnChanges, ViewChild, ElementRef, Input } from '@angular/core';
import * as d3 from 'd3';

declare var $: any;

@Component({
  selector: 'sdrc-line-chart',
  templateUrl: './line-chart.component.html',
  styleUrls: ['./line-chart.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class LineChartComponent implements OnInit, OnChanges {

  @ViewChild('linechart') private chartContainer: ElementRef;
  @Input() private data: ILineData[];
  baselineValue: number

  constructor(private hostRef: ElementRef) { }

  ngOnInit() {
    if (this.data) {
      this.baselineValue = this.data.filter(d=>d.isBaseline)[0].baselineValue
      this.createChart(this.data);
    }
  }

  ngOnChanges(changes) {
    if (this.data && changes.data.previousValue) {
      this.baselineValue = this.data.filter(d=>d.isBaseline)[0].baselineValue
      this.createChart(this.data);
    }
  }


  createChart(data: ILineData[]) {

    data = data.filter(d=>d.isBaseline === false)
    let el = this.chartContainer.nativeElement;
    d3.select(el).selectAll("*").remove();
    var margin = {
      top: 20,
      right: 20,
      bottom: 60,
      left: 40
    }
    let w = $(this.hostRef.nativeElement).parent().width();
    let h = $(this.hostRef.nativeElement).parent().height() - 10

    let width = w - margin.left - margin.right
    let height = h - margin.top - margin.bottom

    var x = d3.scaleBand().range([0, width], 1.0);
    var y = d3.scaleLinear().rangeRound(
      [height, 0]);

    // define the axis
    var xAxis = d3.axisBottom().scale(x).ticks(5);
    var yAxis = d3.axisLeft().scale(y).ticks(5);

    var dataNest = d3.nest().key(function (d) {
      return d.key;
    }).entries(data);

    let max: number = 100
    // let xDomainData: ILineData[] = [];
    // xDomainData = data.filter(d=> d.isBaseline === false)

    x.domain(data.map(function (d) {
        return d.axis;
    }));
    y.domain([0, max]);

    // // Define the line
    var lineFunctionCardinal = d3.line()
      .defined(function (d) { return d && d.value != null; })
      .x(function (d) {
        return x(d.axis) + width / data.length * dataNest.length / 2;
      }).y(function (d) {
        return y(d.value);
      }).curve(d3.curveCardinal);

    // Adds the svg canvas
    var svg = d3.select(el)
    .append("svg")
    .attr("id", "trendsvg")
    .attr("width", w)
    .attr("height", h)
    .append("g")
    .attr("transform", "translate(" + margin.left + "," + (margin.top) + ")")
    .style( "fill", "#000");

    var color = d3.scaleOrdinal().range(["#555"]);

    // add the x-axis
    svg.append("g").attr("class", "x axis")
      .attr(
        "transform", "translate(0," + height + ")")
      .call(xAxis)    
    d3.selectAll(".x.axis .tick text").attr("dx", "0").attr("dy",
      "10").style({
        "text-anchor":
          "middle", "font-size": "11px", "font-weight": "normal"
      });

    svg.selectAll("text");
    svg.append("g").attr("class", "y axis").call(yAxis)
      .append("text").attr("transform", "rotate(-90)")
      .attr("y", -40)
      .attr("x", -height / 2)
      .attr("dy", ".71em")
      .style("text-anchor", "middle")
      .style("fill", "rgb(74, 70, 70)")
      .text("Percentage");

      svg.append("g").attr("class", "x axis")      
      .append("text")
      .attr("y", 230)
      .attr("x", 150)
      .attr("dy", ".71em")
      .style("text-anchor", "middle")
      .style("fill", "rgb(74, 70, 70)")
      .text("Time period")
      .style("font-size", "10px")  
      .style("font-weight", "bold");




      //target line
      svg.append("g").attr("class", "avg").call(yAxis).append("line")          // attach a line
          .attr("stroke-dasharray", "7,7")
          .datum(this.data)
				    .attr("stroke", "#000") 		// colour the line
				    .attr("stroke-width", 1)
				    .attr("fill", "none")
				    .attr("x1", 0)     				// x position of the first end of the line
				    .attr("y1", d=>{
              return y((d[0].target).toString())
            })      // y position of the first end of the line
				    .attr("x2", width)     				// x position of the second end of the line
				    .attr("y2", d=>{
              return y((d[0].target).toString())
            })
            ;


            //base line
            svg.append("g").attr("class", "avg").call(yAxis).append("line")          // attach a line
            .attr("stroke-dasharray", "3,3")
            .datum(this.data)
              .attr("stroke", "#000") 		// colour the line
              .attr("stroke-width", 1)
              .attr("fill", "none")
              .attr("x1", 0)     				// x position of the first end of the line
              .attr("y1", d=>{
                return y((this.baselineValue).toString())
              })      // y position of the first end of the line
              .attr("x2", width)     				// x position of the second end of the line
              .attr("y2", d=>{
                return y((this.baselineValue).toString())
              })
              ;   


              //target text
            svg.append("g")
            .attr("class", "target-text")            
            .append("text")
            .datum(this.data)
            // .attr("x", width - 150)
            .attr("x", d=>{
              if(d[0].value == 0 && d[0].target == 0){
                return width - 100;
              }else{
                return width - 150;
              }
            })
            .attr("y", d=>{
              return y(d[0].target) - 10;
            })
            .text(d=>{
              return "Target(2019-20): " + d[0].target
            })
            .style("font-size", "10px")
            .style("fill", "#d85c54");


            //base value text
            svg.append("g")
            .attr("class", "value-text")            
            .append("text")
            .datum(this.data)
            // .attr("x", width - 230)
            .attr("x", d=>{
              if(d[0].value == 0 && d[0].target == 0){
                return width - 300;
              }else{
                return width - 230;
              }
            })
            .attr("y", d=>{
              if(d[0].value < 10){
                return y(d[0].value) - 10;
              }else{
                return y(d[0].value) + 15;
              }
              
            })
            .text(d=>{
              return "Baseline("+ d[0].src + ":" + d[0].axis +"):" + Math.round(this.baselineValue)
            })
            .style("font-size", "10px")
            .style("fill", "#000");
            
            

    // adding multiple lines in Line chart
    for (let index = 0; index < dataNest.length; index++) {

      let bl = dataNest[index].values[0].isBaseline;

      if(!bl){
      

        var series = svg.append("g")
        .attr("class", "series tag" + dataNest[index].key.split(" ")[0])
        .attr("id", "tag" + dataNest[index].key.split(" ")[0]);

        var path = svg.selectAll(".series#tag" + dataNest[index].key.split(" ")[0])
          .append("path")
          .attr("class", "line tag" + dataNest[index].key.split(" ")[0])
          .attr("id", "tag" + dataNest[index].key.split(" ")[0])
          .attr("d", function (d) {
            return lineFunctionCardinal(dataNest[index].values);
            })
            .style("stroke", "#d85c54")
            .style("stroke-width", "1px")
            .style( "fill", "none")
            .style("cursor", function (d) {
                return "default";
            });

        var totalLength = path.node().getTotalLength();

        path.attr("stroke-dasharray", totalLength + " " + totalLength)
          .attr("stroke-dashoffset", totalLength)        
          .transition()
          .duration(1000)
          .attr("stroke-dashoffset", 0);
        svg.selectAll(".series#tag" + dataNest[index].key.split(" ")[0])
        .select(".point")
        .data(function () {
          return dataNest[index].values;
        })
        .enter()
        .append("circle")
        .attr("id", "tag" + dataNest[index].key.split(" ")[0])
        .attr("class", function (d) {
              return dataNest[index].key.split(" ")[0]
            })
        .attr("cx", function (d) {
                return x(d.axis) + width / data.length * dataNest.length / 2;
              })
        .attr("cy", function (d) {
                return y(d.value);
              })
        .attr("r", function (d) {
                if (d.value == null)
                  return "0px";
                else
                  return "5px";
              })
        .on("mouseover", function (d) {
          showPopover.call(this, d);
        })
        .on("mouseout", function (d) {
          removePopovers();
        })      
        .style("fill", "#d85c54")
        .style("stroke", "none")
        .style("stroke-width", "2px");
              
              


              svg.selectAll(".series#tag" + dataNest[index].key.split(" ")[0])
              .select(".point")
              .data(function () {
                return dataNest[index].values;
              })
              .enter()
              .append("text")
              .attr("x", function (d) {
                return (x(d.axis) + width / data.length * dataNest.length / 2) - 5;
              })
              .attr("y", function (d) {
                return y(d.value) - 10;
              })
              .text(d=>{
                return d.value;
              })
              .style("font-size", "10px");


              
              
              
                    
                    
                            

        // second render pass for the dashed lines
        var left, right
        for (var j = 0; j < dataNest[index].values.length; j += 1) {
          var current = dataNest[index].values[j]
          if (current.value != null) {
            left = current
          } else {
            // find the next value which is not nan
            while (dataNest[index].values[j] != undefined && dataNest[index].values[j].value == null && j < dataNest[index].values.length) j += 1
            right = dataNest[index].values[j]

            if (left != undefined && right != undefined && left.key == right.key) {
              svg.append("path")
                .attr("id", "tag" + dataNest[index].key)
                .attr("class", "tag" + dataNest[index].key)
                .attr("d", lineFunctionCardinal([left, right]))
                .style("stroke", color(dataNest[index].key))
                .attr('stroke-dasharray', '5, 5').style(
                  "fill", "none");
            }
            j -= 1
          }
        }

        
      }
      
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
            return "<div style='color: #257ab6;'> Source: " + d.src + "</div>";
          }
        });
      $(this).popover('show');
    }

    function removePopovers() {
      $('.popover').each(function () {
        $(this).remove();
      });
    }

    d3.selection.prototype.moveToFront = function () {
      return this.each(function () {
        this.parentNode.appendChild(this);
      });
    };
    d3.selectAll(".domain, .y.axis .tick line").style({ "fill": "none", "stroke": "#000" });
    d3.selectAll("circle.point").moveToFront();
  }

}
