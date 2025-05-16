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
	import type {Infer, SuperValidated} from 'sveltekit-superforms';
	import FormCombobox from '$lib/components/form/form-combobox.svelte';
	import { getCustomer } from '../customer/backendRequest';
	import type { SuperForm } from 'sveltekit-superforms';
	import type { CreateSchemaType } from './schema.svelte.ts';

	type CreateReadingForm = Infer<CreateSchemaType>;

	interface CreateDialogProps {
		props: {
			disabled: boolean;
			formData: SuperForm<CreateReadingForm>;
		};
	}

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

	onMount(() => {
		loadReading();
		loadCustomer();
	});

	async function loadReading() {
		readings = (await getReading()) ?? readings;
	}
	async function loadCustomer() {
		customers = (await getCustomer()) ?? [];
	}

	async function createReadingFromForm(form: SuperValidated<any>) {
		const customerId = form.data?.customerId;
		const selectedCustomer = customers.find((c) => c.id === customerId);

		if (!customerId || !selectedCustomer) {
			alert("❌ Bitte wähle einen Kunden aus dem Dropdown-Menü.");
			return;
		}

		form.data.customer = selectedCustomer.id;
		delete form.data.customerId;

		const kindOfMeterMap = ["HEIZUNG", "STROM", "WASSER", "UNBEKANNT"];
		const rawValue = form.data.kindOfMeter;

		if (typeof rawValue === "number" || (typeof rawValue === "string" && !isNaN(Number(rawValue)))) {
			form.data.kindOfMeter = kindOfMeterMap[Number(rawValue)];
		}

		console.log("📦 Absenden mit form.data:", form.data);

		const result = await createReading(form.data);
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
	const customerOptions = $derived(
			customers.map((customer) => ({
				value: customer.id,
				label: `${customer.lastName}, ${customer.firstName}`,
				customer
			}))
	);

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
	{#snippet createDialog({ props }: CreateDialogProps )}

		<FormInput label="Reading ID" placeholder="ID-Number" key="id" {...props.formData} />
		<FormInput label="Comment" placeholder="Add a comment" key="comment" {...props.formData} />
		<FormInput label="Date Of Reading" placeholder="YYYY-MM-DD" key="dateOfReading" {...props.formData} />
		<FormInput
				label="Kind Of Meter"
				placeholder="HEIZUNG, STROM, UNBEKANNT, WASSER"
				key="kindOfMeter"
				{...props.formData}
		/>
		<FormInput
				label="Meter Count"
				placeholder="Enter count"
				key="meterCount"
				type="number"
				{...props.formData}
		/>
		<FormInput label="Meter-ID" placeholder="Meter identifier" key="meterId" {...props.formData} />
	{/snippet}

</SimpleTable>
<p>Kunden gefunden: {customers.length}</p>

