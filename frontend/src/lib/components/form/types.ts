import type { SuperValidated } from 'sveltekit-superforms';
import type { ZodEffects, ZodObject, ZodRawShape } from 'zod';

export interface FormSchema<T extends ZodRawShape> {
  schema: ZodObject<T> | ZodEffects<ZodObject<T>>;
  form: SuperValidated<T, any, T>;
}

export interface Error {
  field?: string;
  error: string;
}
