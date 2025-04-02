<script lang="ts">
	import { Button } from '$lib/components/ui/button/index.js';
	import DataTable from './data-table.svelte';
	import { columns, type Customer } from '$lib/components/customer/columns.js';

	let data: Customer[] = [
		{
			id: '123',
			firstName: 'Tony',
			lastName: 'Testmann',
			gender: 'M',
			birthDate: '01.03.04'
		},
		{
			id: '456',
			firstName: 'Anna',
			lastName: 'Benz',
			gender: 'W',
			birthDate: '02.03.1896'
		}
	];
</script>

{#if columns.length !== 0}
	{console.log(data)}
{/if}
<Button>jefffdsanjfdsla fdsjkfjlds</Button>
<DataTable {data} {columns} />
