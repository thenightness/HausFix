<script lang="ts">
	import Trash from '@lucide/svelte/icons/trash';
	import Pencil from '@lucide/svelte/icons/pencil';
	import { Button } from '../ui/button/index.js';

	interface Props {
		edit: () => void;
		remove: () => void;
	}

	let { edit, remove }: Props = $props();
</script>

<div class="flex w-full space-x-4">
	<Button size="icon" variant="secondary" onclick={edit} class="ml-auto">
		<Pencil />
	</Button>
	<Button size="icon" variant="destructive" onclick={remove}>
		<Trash />
	</Button>
</div>
