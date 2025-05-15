import { createColumn } from '$lib/components/table/helpers.svelte';
import { renderComponent } from '$lib/components/ui/data-table/render-helpers';
import type { ColumnDef } from '@tanstack/table-core';
import Actions from '$lib/components/table/actions.svelte';
import type { Customer, Reading } from './types';

export const columns = (
	edit: (user: string) => void,
	remove: (user: string) => void
): ColumnDef<Reading>[] => [
	createColumn('id', 'Reading-ID'),
	createColumn(
		'customer',
		'Customer',
		(customer: Customer) => `${customer?.lastName}, ${customer?.firstName}`
	),
	createColumn('comment', 'Comment'),
	createColumn('dateOfReading', 'Date Of Reading'),
	createColumn('kindOfMeter', 'Kind Of Meter'),
	createColumn('meterCount', 'Meter Count'),
	createColumn('meterId', 'Meter-ID'),
	createColumn('substitute', 'Substitute'),
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
