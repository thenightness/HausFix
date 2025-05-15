import { createColumn } from '$lib/components/table/helpers.svelte';
import { renderComponent } from '$lib/components/ui/data-table/render-helpers';
import type { ColumnDef } from '@tanstack/table-core';
import Actions from '$lib/components/table/actions.svelte';
import type { Customer } from './types';

export const columns = (
	edit: (user: string) => void,
	remove: (user: string) => void
): ColumnDef<Customer>[] => [
	createColumn('id', 'ID'),
	createColumn('firstName', 'Surname'),
	createColumn('lastName', 'Name'),
	createColumn('birthDate', 'Birthdate'),
	createColumn('gender', 'Gender'),
	{
		accessorKey: 'actions',
		header: () => {},
		cell: ({ row }) => {
			return renderComponent(Actions, {
				edit: () => edit(row.getValue('id')),
				remove: () => remove(row.getValue('id'))
			});
		},
		enableHiding: false
	}
];
