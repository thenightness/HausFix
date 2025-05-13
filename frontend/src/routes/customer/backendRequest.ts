import { ContentType, ResponseType } from '$lib/backend/types.svelte';
import { post, get, put, deletee } from '$lib/backend/util.svelte';
import type { Customer } from './types';

export async function getCustomer() {
	let result = await get<Customer[]>('/customers', ResponseType.Json);
	if (Array.isArray(result)) {
		return result;
	}
}
export async function createCustomer(item: Customer){
	let result = await post<Customer>('/customers', ResponseType.Json, ContentType.Json, JSON.stringify(item));
	if(typeof result !== 'object'){
		return result;
	}
}
export async function updateCustomer(item: Customer){
	return await put<undefined>('/customers', ResponseType.None, ContentType.Json, JSON.stringify(item));
}
export async function deleteCustomer(id: string){
	return await deletee<undefined>(`/customers/${id}`, ResponseType.None, undefined);
}