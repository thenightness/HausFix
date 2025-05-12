<script lang="ts">
  import * as Form from '../ui/form/index.js';
  import { type SuperForm } from 'sveltekit-superforms';
  import { Input } from '../ui/input/index.js';
  import type {
    HTMLInputAttributes,
    HTMLInputTypeAttribute
  } from 'svelte/elements';
  import type { WithElementRef } from 'bits-ui';

  type InputType = Exclude<HTMLInputTypeAttribute, 'file'>;

  type InputProps = WithElementRef<
    Omit<HTMLInputAttributes, 'type'> &
      (
        | { type: 'file'; files?: FileList }
        | { type?: InputType; files?: undefined }
      )
  >;

  interface Props {
    formData: SuperForm<any>;
    key: string;
    label: string;
    disabled?: boolean;
  }

  let {
    formData: form,
    key,
    label,
    disabled,
    ...restProps
  }: InputProps & Props = $props();

  const { form: formData } = $derived(form);
</script>

<Form.Field {form} name={key} class="gap-1/2 grid">
  <Form.Control>
    {#snippet children({ props })}
      <Form.Label>{label}</Form.Label>
      <Input {disabled} {...props} {...restProps} bind:value={$formData[key]} />
    {/snippet}
  </Form.Control>
  <Form.FieldErrors />
</Form.Field>
