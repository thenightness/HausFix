<script lang='ts'>
	import { Button } from "$lib/components/ui/button/index.js";
	import FormInput from '$lib/components/form/form-input.svelte';
	import SimpleTable from '$lib/components/table/simple-table.svelte';
	import { onMount } from 'svelte';
	import type { PageServerData } from './$types';
	import { createSchema, deleteSchema, editSchema } from './schema.svelte';
	import { columns } from './table.svelte';
	import type { Reading } from './types';
	import { createReading, getReading, updateReading,deleteReading } from './backendRequest';
	import type { SuperValidated } from 'sveltekit-superforms';


	interface Props {
		data: PageServerData;
	}

	let { data }: Props = $props();
	let readings: Reading[] = $state([]);

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
</script>

<SimpleTable
	data={readings}
	{columns}
	label="Readings"
	toId={(item) => item.id}
	display={(item) => item?.comment}
	filter_keys={['id','firstName','lastName','gender','birthDate']}
	title="Readings"
	description="view customers"
	createItemFn={createReadingFromForm}
	editItemFn={editReadingFromForm}
	deleteItemFn={deleteReadingFromForm}
	{createForm}
	{editForm}
	{deleteForm}
>
</SimpleTable>
