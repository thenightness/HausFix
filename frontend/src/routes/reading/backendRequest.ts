import { ContentType, ResponseType } from '$lib/backend/types.svelte';
import { post, get, put, deletee } from '$lib/backend/util.svelte';
import type { Read } from '$lib/util/types';
import type { Reading } from './types';

let customerInput;
let startDateInput;
let endDateInput;
let kindOfMeterInput;

export async function getReading(
	props:
		| undefined
		| {
				customerInput: string;
				startDateInput: string;
				endDateInput: string;
				kindOfMeterInput: string;
		  }
): Promise<any> {
	if (props) {
		let { customerInput, startDateInput, endDateInput, kindOfMeterInput } = props;
		let result = await get<Reading[]>(
			`/readings?customer=${customerInput}&start=${startDateInput}&end=${endDateInput}&kindOfMeter=${kindOfMeterInput}`,
			ResponseType.Json
		);
		if (Array.isArray(result)) {
			return result;
		}
	}
}
export async function createReading(item: Reading) {
	let result = await post<Reading>(
		'/readings',
		ResponseType.Json,
		ContentType.Json,
		JSON.stringify(item)
	);
	if (typeof result !== 'object') {
		return result;
	}
}
export async function createRead(item: Read) {
	let result = await post<Read>(
		'/readings',
		ResponseType.Json,
		ContentType.Json,
		JSON.stringify(item)
	);
	if (typeof result !== 'object') {
		return result;
	}
}
export async function updateReading(item: Reading) {
	return await put<undefined>(
		'/readings',
		ResponseType.None,
		ContentType.Json,
		JSON.stringify(item)
	);
}
export async function deleteReading(id: string) {
	return await deletee<undefined>(`/readings/${id}`, ResponseType.None, undefined);
}
