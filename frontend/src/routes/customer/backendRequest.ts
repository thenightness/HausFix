import { ResponseType } from '$lib/backend/types.svelte';
import { get } from '$lib/backend/util.svelte';
import type { Customer } from './types';

export async function getCustomer() {
	let result = await get<Customer[]>('/customers', ResponseType.Json);
	if (Array.isArray(result)) {
		return result;
	}
}