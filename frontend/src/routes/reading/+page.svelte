<script lang="ts">
	import { Button } from '$lib/components/ui/button/index.js';
	import FormInput from '$lib/components/form/form-input.svelte';
	import SimpleTable from '$lib/components/table/simple-table2.svelte';
	import { onMount } from 'svelte';
	import type { PageServerData } from './$types';
	import { createSchema, deleteSchema, editSchema } from './schema.svelte';
	import { columns } from './table.svelte';
	import type { Customer, Reading } from './types';
	import { createReading, getReading, updateReading, deleteReading } from './backendRequest';
	import type { SuperValidated } from 'sveltekit-superforms';
	import FormCombobox from '$lib/components/form/form-combobox.svelte';
	import { Input } from '$lib/components/ui/input';

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

	export async function setReading(r: Reading[]) {
		readings = r;
	}

	async function loadReading() {
		readings =
			(await getReading({ customerInput, startDateInput, endDateInput, kindOfMeterInput })) ??
			readings;
	}
	async function createReadingFromForm(form: SuperValidated<any>) {
		let result = await createReading(form.data);
		readings =
			(await getReading({ customerInput, startDateInput, endDateInput, kindOfMeterInput })) ??
			readings;
		return result;
	}
	async function editReadingFromForm(item: Reading) {
		let result = await updateReading(item);
		readings =
			(await getReading({ customerInput, startDateInput, endDateInput, kindOfMeterInput })) ??
			readings;
		return result;
	}
	async function deleteReadingFromForm(id: string) {
		let result = await deleteReading(id);
		readings =
			(await getReading({ customerInput, startDateInput, endDateInput, kindOfMeterInput })) ??
			readings;
		return result;
	}
	const customerOptions = customers.map((customer) => ({
		value: customer.id, // Der Wert, der im Formular gespeichert wird (die Kunden-ID)
		label: `${customer.lastName}, ${customer.firstName}` // Der Text, der im Dropdown angezeigt wird
	}));

	let customerInput = $state('');
	let startDateInput = $state('');
	let endDateInput = $state('');
	let kindOfMeterInput = $state('');
</script>

<SimpleTable
	data={readings}
	{columns}
	label="Reading"
	toId={(item) => item.id}
	display={(item) => item?.comment}
	title="Readings"
	description="view readings"
	createItemFn={createReadingFromForm}
	editItemFn={editReadingFromForm}
	deleteItemFn={deleteReadingFromForm}
	{createForm}
	{editForm}
	{deleteForm}
>
	{#snippet filter()}
		<Input placeholder="Customer" class="mr-2 max-w-full" bind:value={customerInput} />
		<Input placeholder="Start Date" class="mr-2 max-w-full" bind:value={startDateInput} />
		<Input placeholder="End Date" class="mr-2 max-w-full" bind:value={endDateInput} />
		<Input placeholder="Kind Of Meter" class="mr-2 max-w-full" bind:value={kindOfMeterInput} />
		<Button class="mr-2 max-w-full" onclick={loadReading}>Filtern</Button>
	{/snippet}
	{#snippet createDialog({ props })}
		<FormInput label="Customer ID" placeholder="ID-Number" key="customer.id" {...props} />
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
