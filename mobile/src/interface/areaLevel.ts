// interface IAreaLevel
// {
//     id: string,
//     name: string,
//     level: number,
//     isStateAvailable: boolean,
//     isDistrictAvailable: boolean,
//     slugidarealevel: number,
//     createdDate:string,
//     lastModified:string

// 
interface IAreaLevel
{
    id: string,
    name: string,
    level: number,
    stateAvailable: boolean,
    districtAvailable: boolean,
    slugidarealevel?: number,
    createdDate?:string,
    lastModified?:string

}

