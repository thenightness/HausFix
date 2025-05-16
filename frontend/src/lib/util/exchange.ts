import { ContentType, ResponseType } from '$lib/backend/types.svelte';
import { post, get, put, deletee } from '$lib/backend/util.svelte';
import { createCustom, createCustomer } from '../../routes/customer/backendRequest';
import { Gender, type Customer } from '../../routes/customer/types';
import loadCustomer from '../../routes/customer/+page.svelte';
import { createRead, createReader, getReading } from '../../routes/reading/backendRequest';
import type { Reading } from '../../routes/reading/types';
import type { Custom, Read, Reader } from './types';

export async function downloadCustomer() {
	let result = await get<Customer[]>('/customers', ResponseType.Json);
	if (Array.isArray(result)) {
		customersToCsvAndDownload(result);
	}
	return;
}

export async function downloadReading() {
	let result = await get<Reading[]>('/readings', ResponseType.Json);
	console.log(result)
	if (Array.isArray(result)) {
		readingsToCsvAndDownload(result);
	}
	return;
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
	const header = [
		'ID',
		'Kommentar',
		'Kunden-ID',
		'Datum der Ablesung',
		'Zählertyp',
		'Zählerstand',
		'Zähler-ID',
		'Ersatzwert'
	];
	console.log(readings);
	// 2. Map the Reading objects to CSV rows
	const csvRows = readings.map((reading) => [
		reading.id,
		reading.comment,
		reading.customer.id,
		reading.dateOfReading,
		reading.kindOfMeter,
		reading.meterCount.toString(),
		reading.meterId,
		reading.substitute.toString()
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

export async function parseCsvToCustomer(text: string) {
	let customers = await getCustomersFromCsv(text);
	customers.forEach((value) => {
		console.log(value);
		createCustom(value);
	});
	loadCustomer;
}
export async function getCustomersFromCsv(text: string) {
	const rows = text.trim().split('\n');
	const header = rows.shift()?.split(',') || [];
	const customers: Custom[] = rows.map((row) => {
		const values = row.split(',');
		const customer: Custom = {
			id: values[0], // ID ist jetzt ein String
			firstName: values[1],
			lastName: values[2],
			birthDate: values[3], // Konvertierung zum Date-Objekt
			gender: values[4] as Gender // Typ-Assertion zu Gender
		};
		return customer;
	});
	return customers;
}
export async function parseCsvToReading(text: string) {
	let readings = await getReadingFromCsv(text);
	readings.forEach((value) => {
		console.log(value);
		createReader(value);
	});
	loadCustomer;
}
export async function getReadingFromCsv(text: string) {
	const rows = text.trim().split('\n');
	const header = rows.shift()?.split(',') || [];
	const readings: Reader[] = rows.map((row) => {
		const values = row.split(',');
		const reading: Reader = {
			id: values[0],
			comment: values[1],
			customerId: values[2],
			dateOfReading: values[3],
			kindOfMeter: values[4],
			meterCount: parseInt(values[5]),
			meterId: values[6],
			substitute: !!values[7]
		};
		return reading;
	});
	return readings;
}