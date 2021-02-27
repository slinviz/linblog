---
title: "Mermaidtest"
#description: <descriptive text here>
date: 2021-02-26T22:50:38+08:00
draft: true
#toc: false
#image: ""
tags: []
categories: []
---

# Test Mermaid
## Pie
{{< mermaid >}}
    pie title Pets adopted by volunteers
    "Dogs" : 386
    "Cats" : 85
    "Rats" : 15
{{< /mermaid >}}

## Journel
{{< mermaid >}}
sequenceDiagram
    Alice->>Bob: Hello Bob, how are you?
    alt is sick
        Bob->>Alice: Not so good :(
    else is well
        Bob->>Alice: Feeling fresh like a daisy
    end
    opt Extra response
        Bob->>Alice: Thanks for asking
    end
{{< /mermaid >}}

## Graph
{{< mermaid >}}
    graph TD
    A[Start] --> B{Is it?};
    B -->|Yes| C[OK];
    C --> D[Rethink];
    D --> B;
    B ---->|No| E[End];
{{< /mermaid >}}
