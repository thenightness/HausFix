import { z } from 'zod';
import { Gender } from './types';

export const createSchema = z.object({
});

export const editSchema = z.object({
});

export const deleteSchema = z.object({});

export type CreateSchemaType = typeof createSchema;
export type EditSchemaType = typeof editSchema;
export type DeleteSchemaType = typeof deleteSchema;
