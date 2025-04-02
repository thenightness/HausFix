import type { ColumnDef } from "@tanstack/table-core";

export type Customer = {
    id: string,
    firstName: string,
    lastName: string,
    gender: "M" | "W" | "D" | "U",
    birthDate: string,
};
 
export const columns: ColumnDef<Customer>[] = [
  {
    accessorKey: "firstName",
    header: "First Name",
  },
  {
    accessorKey: "lastName",
    header: "Last Name",
  },
  {
    accessorKey: "gender",
    header: "Gender",
    },
  {
    accessorKey: "birthDate",
    header: "Birth Date",
    }
];