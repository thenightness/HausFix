import { ContentType, ResponseType } from '$lib/backend/types.svelte';
import { post, get, put, deletee } from '$lib/backend/util.svelte';
import type { Reading } from './types';

export async function getReading() {
    let result = await get<Reading[]>('/readings', ResponseType.Json);
    if (Array.isArray(result)) {
        return result;
    }
}
export async function createReading(item: Reading){
    let result = await post<Reading>('/readings', ResponseType.Json, ContentType.Json, item);
    if(typeof result !== 'object'){
        return result;
    }
}
export async function updateReading(item: Reading){
    return await put<undefined>('/readings', ResponseType.None, ContentType.Json, JSON.stringify(item));
}
export async function deleteReading(id: string){
    return await deletee<undefined>(`/readings/${id}`, ResponseType.None, undefined);
}