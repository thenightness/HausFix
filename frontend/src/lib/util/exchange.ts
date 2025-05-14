
import { ContentType, ResponseType } from '$lib/backend/types.svelte';
import { post, get, put, deletee } from '$lib/backend/util.svelte';
import type { Customer } from '../../routes/customer/types';
import type { Reading } from '../../routes/reading/types';


export async function downloadCustomer() {
    let result = await get<Customer[]>('/customers', ResponseType.Json);
    if (Array.isArray(result)) {
        readingsToCsvAndDownload(result);
    }
    return;
}
export async function uploadCustomer() {

}
function readingsToCsvAndDownload(customer: Customer[], filename: string = 'readings.csv'): void {
    // 1. Define the CSV header row
    const header = ['Kunden-ID', 'Vorname', 'Nachname', 'Geburtsdatum', 'Geschlecht'];
  
    // 2. Map the Reading objects to CSV rows
    const csvRows = customer.map(customer => [
      customer.id,
      customer.firstName,
      customer.lastName,
      customer.birthDate, // Format date as needed
      customer.gender,
    ]);
    console.log(customer)
    // 3. Combine header and data rows
    const csvString = [header.join(','), ...csvRows.map(row => row.join(','))].join('\n');
  
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