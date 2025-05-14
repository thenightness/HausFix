<script lang="ts">
	import { Button } from '$lib/components/ui/button/index.js';
	import FormInput from '$lib/components/form/form-input.svelte';
	import SimpleTable from '$lib/components/table/simple-table.svelte';
	import { onMount } from 'svelte';
	import type { PageServerData } from './$types';
	import { createSchema, deleteSchema, editSchema } from './schema.svelte';
	import { columns } from './table.svelte';
	import type { Customer, Reading } from './types';
	import { createReading, getReading, updateReading, deleteReading } from './backendRequest';
	import type { SuperValidated } from 'sveltekit-superforms';
	import FormCombobox from '$lib/components/form/form-combobox.svelte';

	interface Props {
		data: PageServerData;
	}

	let { data }: Props = $props();
	let readings: Reading[] = $state([]);
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

	onMount(loadReading);

	async function loadReading() {
		readings = (await getReading()) ?? readings;
	}
	async function createReadingFromForm(form: SuperValidated<any>) {
		let result = await createReading(form.data);
		readings = (await getReading()) ?? readings;
		return result;
	}
	async function editReadingFromForm(item: Reading) {
		let result = await updateReading(item);
		readings = (await getReading()) ?? readings;
		return result;
	}
	async function deleteReadingFromForm(id: string) {
		let result = await deleteReading(id);
		readings = (await getReading()) ?? readings;
		return result;
	}
	const customerOptions = customers.map((customer) => ({
		value: customer.id, // Der Wert, der im Formular gespeichert wird (die Kunden-ID)
		label: `${customer.lastName}, ${customer.firstName}` // Der Text, der im Dropdown angezeigt wird
	}));
</script>

<SimpleTable
	data={readings}
	{columns}
	label="Reading"
	toId={(item) => item.id}
	display={(item) => item?.comment}
	filter_keys={['id']}
	title="Readings"
	description="view readings"
	createItemFn={createReadingFromForm}
	editItemFn={editReadingFromForm}
	deleteItemFn={deleteReadingFromForm}
	{createForm}
	{editForm}
	{deleteForm}
>
	{#snippet createDialog({ props })}
		<FormCombobox
			label="Reading ID"
			placeholder="ID-Number"
			key="customerid"
			customer={customerOptions}
			{...props}
		/>
		<FormInput label="Reading ID" placeholder="ID-Number" key="id" {...props} />
		<FormInput label="Comment" placeholder="Add a comment" key="comment" {...props} />
		<FormInput label="Date Of Reading" placeholder="YYYY-MM-DD" key="dateOfReading" {...props} />
		<FormInput
			label="Kind Of Meter"
			placeholder="HEIZUNG, STROM, UNBEKANNT, WASSER"
			key="kindOfMeter"
			{...props}
		/>
		<FormInput
			label="Meter Count"
			placeholder="Enter count"
			key="meterCount"
			type="number"
			{...props}
		/>
		<FormInput label="Meter-ID" placeholder="Meter identifier" key="meterId" {...props} />
	{/snippet}
</SimpleTable>
