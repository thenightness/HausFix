<script lang="ts">
	import { Toaster } from 'positron-components/components/ui';
	import '../app.css';
	import {ModeWatcher, setMode} from 'mode-watcher';
	import { onMount } from 'svelte';
	let { children } = $props();

	onMount(() => {
		setMode("dark");
	})
</script>

<ModeWatcher/>
<Toaster richColors={true} closeButton={true} position="top-right"/>

<div class="h-full w-full bg-black">
{@render children()}
</div>
