<script lang="ts">
	import Check from '@lucide/svelte/icons/check';
	import ChevronsUpDown from '@lucide/svelte/icons/chevrons-up-down';
	import { tick } from 'svelte';
	import * as Command from '$lib/components/ui/command/index.js';
	import * as Popover from '$lib/components/ui/popover/index.js';
	import { Button } from '$lib/components/ui/button/index.js';
	import { cn } from '$lib/utils.js';
	import type { WithElementRef } from 'bits-ui';
	import type { HTMLInputTypeAttribute, HTMLInputAttributes } from 'svelte/elements';
	import { writable, derived } from 'svelte/store';
	import type { SuperForm } from 'sveltekit-superforms';

	const {
		formData,
		key,
		label,
		disabled = false,
		placeholder = 'Select a customer...',
		customerDropdownOptions = []
	} = $props();

	const customers = customerDropdownOptions;

	type InputType = Exclude<HTMLInputTypeAttribute, 'file'>;

	type InputProps = WithElementRef<
		Omit<HTMLInputAttributes, 'type'> &
			({ type: 'file'; files?: FileList } | { type?: InputType; files?: undefined })
	>;

	interface ComboboxProps {
		formData: any;
		key: string;
		label: string;
		disabled?: boolean;
		customerDropdownOptions?: { value: string; label: string }[];
	}

	/*interface Props {
		formData: SuperForm<any>;
		key: string;
		label: string;
		disabled?: boolean;
		customer: { value: string; label: string }[];
	}*/

	//let { formData: form, key, label, disabled, ...restProps }: InputProps & Props = $props();

	//const { form: formData } = $derived(form);
	/*const customers = [
		{
			value: 'sveltekit',
			label: 'SvelteKit'
		},
		{
			value: 'next.js',
			label: 'Next.js'
		},
		{
			value: 'nuxt.js',
			label: 'Nuxt.js'
		},
		{
			value: 'remix',
			label: 'Remix'
		},
		{
			value: 'astro',
			label: 'Astro'
		}
	];*/


	let open = $state(false);
	const value = writable<any>(null); // Der ausgewählte Customer

	if (formData?.data?.customerId) {
		value.set(formData.data.customerId);
	}


	let triggerRef = $state<HTMLButtonElement>(null!);

	const selectedValue = derived(value, ($value) =>
			$value ? `${$value.lastName}, ${$value.firstName}` : 'Select a customer...'
	);

	// We want to refocus the trigger button when the user selects
	// an item from the list so users can continue navigating the
	// rest of the form with the keyboard.
	function closeAndFocusTrigger() {
		open = false;
		tick().then(() => {
			triggerRef.focus();
		});
	}
</script>

<Popover.Root bind:open>
	<Popover.Trigger bind:ref={triggerRef}>
		{#snippet child({ props })}
			<Button
				variant="outline"
				class="w-[200px] justify-between"
				{...props}
				role="combobox"
				aria-expanded={open}
			>
				{$selectedValue}
				<ChevronsUpDown class="opacity-50" />
			</Button>
		{/snippet}
	</Popover.Trigger>
	<Popover.Content class="w-[200px] p-0">
		<Command.Root>
			<Command.Input placeholder="Search customer..." class="h-9" />
			<Command.List>
				<Command.Empty>No customer found.</Command.Empty>
				<Command.Group value="customers">
					{#each customers as customer (customer.value)}
						<Command.Item
								value={customer.value}
								onSelect={() => {
								value.set(customer.value); // ✅ nur die UUID setzen
								if (formData?.data) {
								  formData.data.customerId = customer.value; // ✅ UUID in Form speichern
								  console.log('✅ Customer-ID gesetzt:', formData.data.customerId);
								}
							closeAndFocusTrigger();
							}}

						>
							<Check class={cn(value !== customer.value && 'text-transparent')} />
							{customer.label}
						</Command.Item>
					{/each}
				</Command.Group>
			</Command.List>
		</Command.Root>
	</Popover.Content>
</Popover.Root>
