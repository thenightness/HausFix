<script lang="ts" generics="C extends ZodRawShape, E extends ZodRawShape, D extends ZodRawShape, T">
	import type { Error, FormSchema } from '../form/types.js';
	import { createTable } from '../table/helpers2.svelte';
	import Table from '../table/base-table2.svelte';
	import { toast } from 'svelte-sonner';
	import type { ColumnDef, Row } from '@tanstack/table-core';
	import { RequestError } from '$lib/backend/types.svelte';
	import type { Snippet, SvelteComponent } from 'svelte';
	import type { SuperForm, SuperValidated } from 'sveltekit-superforms';
	import type { ZodRawShape } from 'zod';
	import FormDialog from '../form/form-dialog.svelte';
	import { Button } from '../ui/form/index.js';
	import Upload from '@lucide/svelte/icons/upload';
	import Download from '@lucide/svelte/icons/download';
	import { getCustomer } from '../../../routes/customer/backendRequest.js';
	import { downloadCustomer, downloadReading } from '$lib/util/exchange.js';

	interface Props {
		data: T[] | undefined;
		columns: (editFn: (id: string) => void, deleteFn: (id: string) => void) => ColumnDef<T>[];
		label: string;
		createItemFn: (form: SuperValidated<C>) => Promise<RequestError | undefined>;
		editItemFn: (item: T) => Promise<RequestError | undefined>;
		deleteItemFn: (id: string) => Promise<RequestError | undefined>;
		toId: (item: T) => string;
		display: (item: T | undefined) => string | undefined;
		title: string;
		description: string;
		editDialog?: Snippet<
			[
				{
					props: {
						disabled: boolean;
						formData: SuperForm<E>;
					};
					item: T;
				}
			]
		>;
		createDialog?: Snippet<
			[
				{
					props: {
						disabled: boolean;
						formData: SuperForm<C>;
					};
				}
			]
		>;
		createForm: FormSchema<C>;
		editForm: FormSchema<any>;
		deleteForm: FormSchema<D>;
		startCreate?: () => boolean | Promise<boolean>;
		startEdit?: (item: T) => void | Promise<void>;
		createClass?: string;
		editClass?: string;
		errorMappings?: {
			[key in RequestError]?: Error;
		};
		filter?: Snippet;
	}

	let {
		data,
		columns,
		label,
		createItemFn,
		editItemFn,
		deleteItemFn,
		toId,
		display,
		title,
		description,
		editDialog,
		createDialog,
		createForm,
		editForm,
		deleteForm,
		startCreate,
		startEdit,
		createClass,
		editClass,
		errorMappings,
		filter
	}: Props = $props();

	let isLoading = $state(false);
	let labelLower = $derived(label.toLocaleLowerCase());
	let fieldProps = $derived({ disabled: isLoading });

	let selected: T | undefined = $state();
	let editOpen = $state(false);
	let deleteOpen = $state(false);

	let editComp: SvelteComponent | undefined = $state();
	let setEditValue: (value: { [key: string]: string }) => void = $derived(
		editComp?.setValue || (() => {})
	);

	let table = $state(
		createTable(
			[],
			columns(
				() => {},
				() => {}
			)
		)
	);

	$effect(() => {
		table = createTable(data || [], columns(editItem, deleteItem));
	});

	const createItem = async (form: SuperValidated<C>) => {
		let ret = await createItemFn(form);

		if (ret) {
			if (errorMappings && errorMappings[ret]) {
				return errorMappings[ret];
			} else {
				return { error: `Error while creating ${labelLower}` };
			}
		} else {
			toast.success(`Created ${label}`);
		}
	};

	const editItem = (id: string) => {
		selected = data?.find((item) => toId(item) === id);
		if (selected) {
			setEditValue(selected);
			startEdit?.(selected);
		}
		editOpen = true;
	};

	const deleteItem = (id: string) => {
		selected = data?.find((item) => toId(item) === id);
		deleteOpen = true;
	};

	const editItemConfirm = async (form: SuperValidated<E>) => {
		selected = {
			...selected,
			...form.data
		} as any;

		if (!selected) {
			return;
		}

		let ret = await editItemFn(selected);

		if (ret) {
			if (errorMappings && errorMappings[ret]) {
				return errorMappings[ret];
			} else {
				return { error: `Error while updating ${labelLower}` };
			}
		} else {
			toast.success(`${label} updated`);
		}
	};

	const deleteItemConfirm = async () => {
		if (!selected) {
			return;
		}

		let ret = await deleteItemFn(toId(selected));

		if (ret) {
			return { error: `Error while deleting ${label}` };
		} else {
			toast.success(`${label} deleted`);
		}
	};
</script>

<FormDialog
	title={`Delete ${label}`}
	description={`Do you really want to delete the ${labelLower} ${display(selected)}?`}
	confirm="Delete"
	confirmVariant="destructive"
	onsubmit={deleteItemConfirm}
	bind:open={deleteOpen}
	bind:isLoading
	form={deleteForm}
/>
<FormDialog
	bind:this={editComp}
	title={`Edit ${label}`}
	description={`Edit the ${labelLower} info for ${display(selected)} below`}
	confirm="Confirm"
	onsubmit={editItemConfirm}
	bind:open={editOpen}
	bind:isLoading
	form={editForm}
	class={editClass}
>
	{#snippet children({ props })}
		{#if selected}
			{@render editDialog?.({
				props: { ...props, ...fieldProps },
				item: selected
			})}
		{/if}
	{/snippet}
</FormDialog>
<div class="m-4 flex flex-1 flex-col space-y-3">
	<div class="ml-7 md:m-0">
		<h3 class="text-xl font-medium">{title}</h3>
		<p class="text-sm text-muted-foreground">
			{description}
		</p>
		<Button class="float-right mx-2 w-24" onclick={downloadReading}><Download /></Button>
		<Button class="float-right mx-2 w-24" onclick={downloadReading}><Upload /></Button>
	</div>
	<Table {table} class="min-h-0 flex-1"  {filter}>
		<FormDialog
			title={`Create ${label}`}
			description={`Enter the details for the new ${labelLower} below`}
			confirm="Create"
			trigger={{
				text: `Create ${label}`,
				variant: 'secondary',
				class: 'ml-2'
			}}
			onsubmit={createItem}
			onopen={startCreate}
			bind:isLoading
			form={createForm}
			class={createClass}
		>
			{#snippet children({ props })}
				{@render createDialog?.({ props: { ...props, ...fieldProps } })}
			{/snippet}
		</FormDialog>
	</Table>
</div>
