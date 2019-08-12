interface MultiLineChart{
    id : string,
    parentAreaName?:string,
    values:LineChart[],
    baseLine?:number,
    target?:number,
    tp?:string,
    src?:string
}