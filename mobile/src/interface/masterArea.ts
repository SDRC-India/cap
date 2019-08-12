interface Area
{
    areaname: string;
    id: string;
    code: string;
    levels?: number[];
    parentAreaCode: string;
    areaLevel : IAreaLevel;
    concatenedName?:string;
    checked?:boolean;
    slugidarea?:number;
}
