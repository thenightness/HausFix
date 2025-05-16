import { z } from 'zod';
import { Gender, KindOfMeter } from './types';

const customerSchema = z.object({
	id: z.string().uuid().optional(),
	firstName: z.string(),
	lastName: z.string(),
	birthDate: z.string(),
	gender: z.nativeEnum(Gender)
});

export const createSchema = z.object({
	id: z.string().uuid().optional(),
	customerId: z.string().uuid(),
	comment: z.string(),
	dateOfReading: z.string().date(),
	kindOfMeter: z
		.union([z.string(), z.number()])
		.transform((val) => typeof val === 'string' ? parseInt(val, 10) : val)
		.refine((val) => Object.values(KindOfMeter).includes(val), {
			message: 'Invalid enum value'
		}),
	meterCount: z.number(),
	meterId: z.string(),
	substitute: z.boolean()
});

export const editSchema = z.object({});

export const deleteSchema = z.object({});

export type CreateSchemaType = typeof createSchema;
export type EditSchemaType = typeof editSchema;
export type DeleteSchemaType = typeof deleteSchema;
