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
