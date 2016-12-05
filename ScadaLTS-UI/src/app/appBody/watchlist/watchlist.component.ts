import {Component, OnInit} from '@angular/core';
import {Http} from '@angular/http';
import * as c3 from "c3";

@Component({
  selector: 'watchlist',
  templateUrl: './watchlist.component.html',
  styleUrls: ['./watchlist.component.css']
})

export class WatchlistComponent implements OnInit {

  items;

  constructor(private http: Http) {
    this.http.get('/app/appBody/watchlist/items.json')
      .subscribe(res => this.items = res.json());
  };

  watchlist = [];

  private addItemToWatchlist(item) {
    if (this.watchlist.indexOf(item) == -1)
      this.watchlist.push(item);
  };


  private start() {
    // let canvas = d3.select("#chart").append("svg").attr("width", 400).attr("height", 400);
    //
    // let circle = canvas.append("circle").attr("cx", 10).attr("cy", 10).attr("r", 50).attr("fill", "blue");

    // d3.json("elements.json", function(data){
    //   let radius = 100;
    //   let color = d3.scale.ordinal().range(['red', 'orange', 'yellow', 'green', 'blue', 'indigo', 'violet']);
    //
    //   let canvas = d3.select("#chart").append('svg').attr('width', 1000).attr('height', 1000);
    //
    //   let group = canvas.append('g').attr('transform', 'translate(500,350)');
    //
    //   let arc = d3.svg.arc().innerRadius(150).outerRadius(radius);
    //
    //   let pie = d3.layout.pie().value(function(d){
    //     return d.rank;
    //   });
    //
    //   let theArc = group.selectAll(".arc").data(pie(data)).enter().append("g").attr("class", "arc");
    //
    //   // theArc.append("path").attr("d", arc).attr("fill", function(d){
    //   //   return color(d.data.rank);
    //   // });
    //
    //   theArc.append("text").attr("transform", function(d) {
    //     return "translate(" + arc.centroid(d) + ")";
    //   }).attr("dy", "0.15em").text(function(d){
    //     return d.data.name;
    //   });
    // });

    // let canvas = d3.select("#chart").append("svg").attr("width", 500).attr("height", 500);
    //
    // let orangeData = [10, 30, 50, 100];
    //
    // let oranges = canvas.selectAll("circle").data(orangeData).enter().append("circle").attr("fill", "orange").attr("cx", function(d,i){
    //   return d + (i * 100);
    // }).attr("cy", function(d){
    //   return d;
    // }).attr("r", function(d){
    //   return d;
    // });
    //
    // let chart = c3.generate({
    //   bindto: '#chart',
    //   data: {
    //     columns: [
    //       ['data1', 30, 200, 100, 400, 150, 250],
    //       ['data2', 50, 20, 10, 40, 15, 25]
    //     ]
    //   }
    // });

    let chart = c3.generate({
      bindto: '#chart',
      data: {
        columns: [
          ['data1', 30, 200, 100, 400, 150, 250, 130, 50, 20, 10, 40, 15, 25, 390],
          ['data2', 50, 20, 10, 40, 15, 25, 542, 30, 200, 100, 333, 150, 250]
        ],
        type: 'spline'
      },

      grid: {
        x: {
          show: false
        },
        y: {
          show: true
        }
      },
      zoom: {
        enabled: true
      }

    });

  }


  ngOnInit() {
    this.start();
  }

  // type = 'line';
  // data = {
  //   labels: ["January", "February", "March", "April", "May", "June", "July"],
  //   datasets: [
  //     {
  //       label: "Watchlist chart",
  //       data: [65, 59, 80, 81, 56, 55, 40],
  //       backgroundColor: 'transparent',
  //       borderColor: '#0099ff',
  //       borderWidth: 1,
  //       lineTension: 0
  //     },
  //     {
  //       label: 'Line Component',
  //       data: [30, 20, 10, 44, 12, 76, 58],
  //       backgroundColor: 'transparent',
  //       borderColor: '#00664d',
  //       borderWidth: 1,
  //       lineTension: 0
  //     },
  //     {
  //       label: "Watchlist chart",
  //       data: [54, 17, 8, 11, 65, 32, 48],
  //       backgroundColor: 'transparent',
  //       borderColor: '#660066',
  //       borderWidth: 1,
  //       lineTension: 0
  //     },
  //     {
  //       label: "Watchlist chart",
  //       data: [7, 92, 23, 32, 75, 34, 98],
  //       backgroundColor: 'transparent',
  //       borderColor: '#b30000',
  //       borderWidth: 1,
  //       lineTension: 0
  //     }
  //   ]
  // };
  //
  // options = {
  //   responsive: true,
  //   maintainAspectRatio: false,
  //   legend: {
  //     display: true,
  //     labels: {
  //       fontColor: 'rgb(255, 99, 132)',
  //       fontFamily: 'Raleway'
  //     }
  //   }
  // };
  //
  // legend:boolean = true;


}
