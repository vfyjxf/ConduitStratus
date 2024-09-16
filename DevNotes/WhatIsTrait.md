# What Is Trait?

- Trait是对于特定类型的IO处理的抽象，它是一个接口，定义了一系列的方法，用于处理特定类型的IO操作。
- Trait附着于Node的特定某个面上，用于处理该面上的IO操作。
- 每一种被处理类型都有单独的Trait频道，例如物品和流体的频道是独立的
- Trait拥有IO标识，表示它所处理的IO类型，共四种Input、Output、Both、None
- Trait拥有频道标识，只有在同一个频道内的trait可以进行IO间的交互
- Trait拥有优先级标识
- Trait拥有plugin，plugin可以约束是否进行IO，操作的数据数量等