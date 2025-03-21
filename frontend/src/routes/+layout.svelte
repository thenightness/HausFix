<script lang="ts">
	import { Sidebar } from 'positron-components/components/ui';
	import AppSidebar from '$lib/components/app-sidebar.svelte';
	import '../app.css';
	let { children } = $props();
</script>

<Sidebar.Provider>
	<AppSidebar />
	<main>
		{@render children?.()}
	</main>
</Sidebar.Provider>
