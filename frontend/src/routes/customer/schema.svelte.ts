import { z } from 'zod';
import { Gender } from './types';

export const createSchema = z.object({
	id: z.string().uuid().optional(),
	firstName: z.string().min(1, 'Surname is required'),
	lastName: z.string().min(1, 'Name is required'),
	birthDate: z.string().date(),
	gender: z.nativeEnum(Gender)
});

export const editSchema = z.object({
	firstName: z.string().min(1, 'Surname is required'),
	lastName: z.string().min(1, 'Name is required'),
	birthDate: z.string().date(),
	gender: z.nativeEnum(Gender)
});

export const deleteSchema = z.object({});

export type CreateSchemaType = typeof createSchema;
export type EditSchemaType = typeof editSchema;
export type DeleteSchemaType = typeof deleteSchema;
