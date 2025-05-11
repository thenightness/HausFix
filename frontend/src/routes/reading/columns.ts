import type { ColumnDef } from "@tanstack/table-core";
 
export type reading = {
 id: string;
 uuid: string;
 firstName: string;
 lastName: string;
 birthDate: string;
 gender: "D" | "M" | "U" | "W";
 dateOfReading: string;
 comment: string;
 meterId: string;
 substitute: boolean;
 meterCount: number;
 kindOfMeter: "HEIZUNG" | "STROM" | "WASSER" | "UNBEKANNT";
};
 
export const columns: ColumnDef<reading>[] = [
 {
  accessorKey: "id",
  header: "id",
 },
 {
  accessorKey: "uuid",
  header: "uuid",
 },
 {
  accessorKey: "firstName",
  header: "firstName",
 },
 {
  accessorKey: "lastName",
  header: "lastName",
 },
 {
  accessorKey: "birthDate",
  header: "birthDate",
 },
 {
  accessorKey: "gender",
  header: "gender",
 },
 {
  accessorKey: "dateOfReading",
  header: "dateOfReading",
 },
 {
  accessorKey: "comment",
  header: "comment",
 },
 {
  accessorKey: "meterId",
  header: "meterId",
 },
 {
  accessorKey: "substitute",
  header: "substitute",
 },
 {
  accessorKey: "meterCount",
  header: "meterCount",
 },
 {
  accessorKey: "kindOfMeter",
  header: "kindOfMeter",
 },
];