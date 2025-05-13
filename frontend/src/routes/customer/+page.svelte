<script lang="ts">
	import SimpleTable from '$lib/components/table/simple-table.svelte';
	import type { PageServerData } from './$types';
	import { createSchema, deleteSchema, editSchema } from './schema.svelte';
	import { columns } from './table.svelte';
	import type { Customer } from './types';

	interface Props {
		data: PageServerData;
	}

	let { data }: Props = $props();
	let customers: Customer[] = $state([]);

	const createForm = {
		schema: createSchema,
		form: data.createForm
	};

	const editForm = {
		schema: editSchema,
		form: data.editForm
	};

	const deleteForm = {
		schema: deleteSchema,
		form: data.deleteForm
	};
</script>

<SimpleTable
	data={customers}
	{columns}
	label="Customer"
	toId={(item) => item.id}
	display={(item) => item?.lastName}
	title="Customers"
	description="view customers"
	{createForm}
	{editForm}
	{deleteForm}
></SimpleTable>
