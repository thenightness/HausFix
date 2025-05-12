import { createColumn } from '$lib/components/table/helpers.svelte';
import { renderComponent } from '$lib/components/ui/data-table/render-helpers';
import type { ColumnDef } from '@tanstack/table-core';
import Actions from '$lib/components/table/actions.svelte';

export const columns = (
	edit: (user: string) => void,
	remove: (user: string) => void
): ColumnDef<Customer>[] => [
	createColumn('name', 'Name'),
	createColumn('uuid', 'Uuid'),
	{
		accessorKey: 'actions',
		header: () => {},
		cell: ({ row }) => {
			return renderComponent(Actions, {
				edit: () => edit(row.getValue('uuid')),
				remove: () => remove(row.getValue('uuid'))
			});
		},
		enableHiding: false
	}
];
