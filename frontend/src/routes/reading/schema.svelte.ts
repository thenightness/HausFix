import { z } from 'zod';
import { Gender, KindOfMeter } from './types';

export const createSchema = z.object({
    customerId: z.string().uuid(),
	comment: z.string().optional(),
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
