export interface Custom {
	id: string;
	firstName: string;
	lastName: string;
	birthDate: string;
	gender: string;
}

export interface Read {
	id: string;
	comment: string;
	customer: {
		id: string;
		firstName: string;
		lastName: string;
		birthDate: string;
		gender: string;
	};
	dateOfReading: string;
	kindOfMeter: string;
	meterCount: number;
	meterId: string;
	substitute: boolean;
}

export interface Reader {
	id: string;
	comment: string;
	customerId: string;
	dateOfReading: string;
	kindOfMeter: string;
	meterCount: number;
	meterId: string;
	substitute: boolean;
}