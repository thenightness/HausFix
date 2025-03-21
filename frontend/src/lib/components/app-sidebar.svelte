<script lang="ts">
    import Home from "@lucide/svelte/icons/house";
    import Customer from "@lucide/svelte/icons/book-user";
    import Reading from "@lucide/svelte/icons/table-properties";
    import * as Sidebar from "$lib/components/ui/sidebar/index.js";

    const items = [
        {
            title: "Dashboard",
            url: "/",
            icon: Home,
        },
        {
            title: "Kunden",
            url: "customer",
            icon: Customer,
        },
        {
            title: "Ablesungen",
            url: "reading",
            icon: Reading,
        },
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
                        <item.icon/>
                        <span>{item.title}</span>
                        </a>
                    {/snippet}
                    </Sidebar.MenuButton>
                </Sidebar.MenuItem>
                {/each}
            </Sidebar.Menu>
            </Sidebar.GroupContent>
        </Sidebar.Group>
        </Sidebar.Content>
    <Sidebar.Footer>
        <Sidebar.Trigger/>
    </Sidebar.Footer>
</Sidebar.Root>