import { z } from 'zod';
import { Gender, KindOfMeter } from './types';

const customerSchema = z.object({
	id: z.string().uuid().optional(),
	firstName: z.string().optional(),
	lastName: z.string().optional(),
	birthDate: z.string().optional(),
	gender: z.nativeEnum(Gender).optional()
});

export const createSchema = z.object({
	id: z.string().uuid().optional(),
    customer: customerSchema,
	comment: z.string(),
	dateOfReading: z.string().date(),
	kindOfMeter: z.nativeEnum(KindOfMeter),
	meterCount: z.number(),
	meterId: z.string(),
	substitute: z.boolean()
});

export const editSchema = z.object({});

export const deleteSchema = z.object({});

export type CreateSchemaType = typeof createSchema;
export type EditSchemaType = typeof editSchema;
export type DeleteSchemaType = typeof deleteSchema;
