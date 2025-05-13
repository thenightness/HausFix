<script lang="ts">
	import FormInput from '$lib/components/form/form-input.svelte';
	import SimpleTable from '$lib/components/table/simple-table.svelte';
	import { onMount } from 'svelte';
	import type { PageServerData } from './$types';
	import { createSchema, deleteSchema, editSchema } from './schema.svelte';
	import { columns } from './table.svelte';
	import type { Customer } from './types';
	import { createCustomer, getCustomer, updateCustomer,deleteCustomer } from './backendRequest';
	import type { SuperValidated } from 'sveltekit-superforms';

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

	onMount(loadCustomer);

	async function loadCustomer() {
		customers = (await getCustomer()) ?? customers;
	}
	async function createCustomerFromForm(form: SuperValidated<any>) {
		let result = await createCustomer(form.data);
		customers = (await getCustomer()) ?? customers;
		return result;
	}
	async function editCustomerFromForm(item: Customer) {
		let result = await updateCustomer(item);
		customers = (await getCustomer()) ?? customers;
		return result;
	}
	async function deleteCustomerFromForm(id: string) {
		let result = await deleteCustomer(id);
		customers = (await getCustomer()) ?? customers;
		return result;
	}
</script>

<SimpleTable
	data={customers}
	{columns}
	label="Customer"
	toId={(item) => item.id}
	display={(item) => item?.lastName}
	filter_keys={['id','firstName','lastName','gender','birthDate']}
	title="Customers"
	description="view customers"
	createItemFn={createCustomerFromForm}
	editItemFn={editCustomerFromForm}
	deleteItemFn={deleteCustomerFromForm}
	{createForm}
	{editForm}
	{deleteForm}
>
	{#snippet createDialog({ props })}
		<FormInput label="ID" placeholder="ID-Number" key="id" {...props} />
		<FormInput label="Surname" placeholder="Max" key="firstName" {...props} />
		<FormInput label="Name" placeholder="Mustermann" key="lastName" {...props} />
		<FormInput label="Gender" placeholder="D - M - U - W" key="gender" {...props} />
		<FormInput label="Birthdate" placeholder="1990-01-01" key="birthDate" {...props} />
	{/snippet}
	{#snippet editDialog({ props, item })}
		<FormInput label="Surname" placeholder="Max" key="firstName" {...props} />
		<FormInput label="Name" placeholder="Mustermann" key="lastName" {...props} />
		<FormInput label="Gender" placeholder="D - M - U - W" key="gender" {...props} />
		<FormInput label="Birthdate" placeholder="1990-01-01" key="birthDate" {...props} />
	{/snippet}
</SimpleTable>
