export interface Reading {
	id: string;
	comment: string;
	customer: Customer;
	dateOfReading: Date;
	kindOfMeter: KindOfMeter;
	meterCount: number;
	meterId: string;
	substitute: boolean;
}

export interface Customer {
	id: string;
	firstName: string;
	lastName: string;
	birthDate: Date;
	gender: Gender;
}
export enum Gender {
	NonBinary = 'D',
	Male = 'M',
	Unknown = 'U',
	Female = 'W'
}

export enum KindOfMeter{
	HEIZUNG = 'HEIZUNG',
	STROM = 'STROM',
	UNBEKANNT = 'UNBEKANNT',
	WASSER = 'WASSER'
}
