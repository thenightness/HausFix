<script lang="ts">
  import * as Form from '../ui/form/index.js';
  import { type SuperForm } from 'sveltekit-superforms';
  import type { HTMLInputAttributes } from 'svelte/elements';
  import Multiselect, {
    type Group,
    type Item
  } from '../table/multiselect.svelte';

  type T = $$Generic;

  interface Props {
    formData: SuperForm<any>;
    key: string;
    label: string;
    disabled?: boolean;
    data: Group<T>[] | Item<T>[];
    filter?: (data: Item<T>) => boolean;
    compare?: (a: T, b: T) => boolean;
    single?: boolean;
  }

  let {
    formData: form,
    key,
    label,
    disabled,
    ...restProps
  }: HTMLInputAttributes & Props = $props();

  const { form: formData } = $derived(form);
</script>

<Form.Field {form} name={key} class="gap-1/2 grid">
  <Form.Control>
    {#snippet children({ props })}
      <Form.Label>{label}</Form.Label>
      <Multiselect
        {disabled}
        {label}
        {...props}
        {...restProps}
        bind:selected={$formData[key]}
      />
    {/snippet}
  </Form.Control>
  <Form.FieldErrors />
</Form.Field>
