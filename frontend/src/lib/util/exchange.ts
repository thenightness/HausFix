import { ContentType, ResponseType } from '$lib/backend/types.svelte';
import { post, get, put, deletee } from '$lib/backend/util.svelte';
import type { Customer } from '../../routes/customer/types';
import { getReading } from '../../routes/reading/backendRequest';
import type { Reading } from '../../routes/reading/types';

export async function downloadCustomer() {
	let result = await get<Customer[]>('/customers', ResponseType.Json);
	if (Array.isArray(result)) {
		customersToCsvAndDownload(result);
	}
	return;
}

export async function downloadReading() {
	let result = await get<Reading[]>('/readings', ResponseType.Json);
	if (Array.isArray(result)) {
		readingsToCsvAndDownload(result);
	}
	return;
}
export async function uploadCustomer() {
	
}
function customersToCsvAndDownload(customer: Customer[], filename: string = 'customer.csv'): void {
	// 1. Define the CSV header row
	const header = ['Kunden-ID', 'Vorname', 'Nachname', 'Geburtsdatum', 'Geschlecht'];

	// 2. Map the Reading objects to CSV rows
	const csvRows = customer.map((customer) => [
		customer.id,
		customer.firstName,
		customer.lastName,
		customer.birthDate, // Format date as needed
		customer.gender
	]);
	// 3. Combine header and data rows
	const csvString = [header.join(','), ...csvRows.map((row) => row.join(','))].join('\n');

	// 4. Create a Blob
	const blob = new Blob([csvString], { type: 'text/csv;charset=utf-8;' });

	// 5. Create a temporary link
	const link = document.createElement('a');
	const url = URL.createObjectURL(blob);
	link.href = url;
	link.download = filename;
	document.body.appendChild(link);

	// 6. Trigger the download
	link.click();

	// 7. Clean up
	document.body.removeChild(link);
	URL.revokeObjectURL(url);
}

function readingsToCsvAndDownload(readings: Reading[], filename: string = 'readings.csv'): void {
	// 1. Define the CSV header row
	const header = ['ID', 'Kommentar', 'Kunden-ID', 'Vorname', 'Nachname', 'Geburtsdatum', 'Geschlecht', 'Datum der Ablesung', 'Zählertyp', 'Zählerstand', 'Zähler-ID', 'Ersatzwert'];
	console.log(1);
	// 2. Map the Reading objects to CSV rows
	const csvRows = readings.map(reading => [
	  reading.id,
	  reading.comment,
	  reading.customer.id,
	  reading.customer.firstName,
	  reading.customer.lastName,
	  reading.customer.birthDate.toLocaleDateString(), // Format date as needed
	  reading.customer.gender,
	  reading.dateOfReading.toLocaleDateString(),     // Format date as needed
	  reading.kindOfMeter,
	  reading.meterCount.toString(),
	  reading.meterId,
	  reading.substitute.toString()
	]);
	console.log(2);
	// 3. Combine header and data rows
	const csvString = [header.join(','), ...csvRows.map(row => row.join(','))].join('\n');
	console.log(3);
	// 4. Create a Blob
	const blob = new Blob([csvString], { type: 'text/csv;charset=utf-8;' });
	console.log(4);
	// 5. Create a temporary link
	const link = document.createElement('a');
	const url = URL.createObjectURL(blob);
	link.href = url;
	link.download = filename;
	document.body.appendChild(link);
	console.log(5);
	// 6. Trigger the download
	link.click();
	console.log(6);
	// 7. Clean up
	document.body.removeChild(link);
	URL.revokeObjectURL(url);
	console.log(7);
  }