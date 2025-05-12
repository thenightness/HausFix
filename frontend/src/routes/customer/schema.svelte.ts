import { z } from 'zod';

export const createSchema = z.object({
	name: z.string().min(1, 'Name is required')
});

export const editSchema = z.object({
	name: z.string().min(1, 'Name is required')
});

export const deleteSchema = z.object({});

export type CreateSchemaType = typeof createSchema;
export type EditSchemaType = typeof editSchema;
export type DeleteSchemaType = typeof deleteSchema;
