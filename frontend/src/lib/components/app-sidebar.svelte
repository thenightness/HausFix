<script lang="ts">
	import Home from '@lucide/svelte/icons/house';
	import Customer from '@lucide/svelte/icons/book-user';
	import Reading from '@lucide/svelte/icons/table-properties';
	import Upload from '@lucide/svelte/icons/upload';
	import Download from '@lucide/svelte/icons/download';
	import * as Sidebar from '$lib/components/ui/sidebar/index.js';
	import Button from './ui/button/button.svelte';

	const items = [
		{
			title: 'Dashboard',
			url: '/',
			icon: Home
		},
		{
			title: 'Kunden',
			url: 'customer',
			icon: Customer
		},
		{
			title: 'Ablesungen',
			url: 'reading',
			icon: Reading
		}
	];
	const option = [
		{
			upload: Upload,
			download: Download
		}
	];
</script>

<Sidebar.Root collapsible="icon">
	<Sidebar.Header class="group-data-[collapsible=icon]:hidden">
		<Sidebar.Menu>
			<Sidebar.MenuItem>
				<span>HausFix</span>
			</Sidebar.MenuItem>
		</Sidebar.Menu>
	</Sidebar.Header>
	<Sidebar.Content>
		<Sidebar.Group>
			<Sidebar.GroupContent>
				<Sidebar.Menu>
					{#each items as item (item.title)}
						<Sidebar.MenuItem>
							<Sidebar.MenuButton>
								{#snippet child({ props })}
									<a href={item.url} {...props}>
										<item.icon />
										<span>{item.title}</span>
									</a>
								{/snippet}
							</Sidebar.MenuButton>
							{#if item.title !== 'Dashboard'}
                                <Button class="float-right w-24 mx-2" href={item.url}><Download /></Button>
								<Button class="float-right w-24 mx-2" href={item.url}><Upload /></Button>
							{/if}
						</Sidebar.MenuItem>
					{/each}
				</Sidebar.Menu>
			</Sidebar.GroupContent>
		</Sidebar.Group>
	</Sidebar.Content>
	<Sidebar.Footer class="flex">
		<Sidebar.Trigger class="ml-auto" />
		<!--Margin left auto-->
	</Sidebar.Footer>
</Sidebar.Root>
